/// ///////////////////////////////////////////////////////////////////////////
// AbstractWebSocketServerEventReceiver.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.websocket.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.Session;
import strata.foundation.core.event.AbstractEventReceiver;
import strata.foundation.core.event.IEventListener;
import strata.foundation.core.mapper.ObjectMapperSupplier;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract
class AbstractWebSocketServerEventReceiver<E,L extends IEventListener<E>>
    extends AbstractEventReceiver<E,L>
{
    private final IWebSocketSessionMap sessions;
    private final Class<E>            eventType;
    private final String              path;
    private final ObjectMapper        mapper;
    private final Set<String>         registeredSessionIds;
    private ExecutorService           executor;
    private AtomicBoolean             listening;

    protected AbstractWebSocketServerEventReceiver(
        IWebSocketSessionMap sessions,
        Class<E>             eventType,
        String               path)
    {
        this.sessions             = sessions;
        this.eventType            = eventType;
        this.path                 = path;
        this.mapper               = new ObjectMapperSupplier().get();
        this.registeredSessionIds = ConcurrentHashMap.newKeySet();
        this.executor             = Executors.newSingleThreadExecutor();
        this.listening            = new AtomicBoolean(false);
    }

    @Override
    public void
    startListening()
    {
        if (isListening())
            return;

        if (!hasListener())
            throw new IllegalStateException("No listener.");

        listening.set(true);
        executor.execute(this::runListeningLoop);
    }

    @Override
    public void
    stopListening()
    {
        listening.set(false);
    }

    @Override
    public boolean
    isListening()
    {
        return listening.get();
    }

    private void
    runListeningLoop()
    {
        try
        {
            getListener().ifPresent(
                listener ->
                {
                    try { listener.onStart(); }
                    catch (Exception e) { listener.onException(e); }
                });

            while (listening.get())
            {
                registerNewSessions();
                Thread.sleep(100);
            }
        }
        catch (InterruptedException interrupted) {}
        catch (Exception exception)
        {
            getListener().ifPresent(
                listener -> listener.onException(exception));
        }
        finally
        {
            registeredSessionIds.clear();
            getListener().ifPresent(IEventListener::onStop);
        }
    }

    private void
    registerNewSessions()
    {
        Set<Session> openSessions = sessions.getOpen(path);

        for (Session session : openSessions)
        {
            if (registeredSessionIds.add(session.getId()))
                session.addMessageHandler(
                    String.class,
                    (String payload) ->
                        getListener().ifPresent(
                            listener ->
                            {
                                try
                                {
                                    E event =
                                        mapper.readValue(payload,eventType);

                                    listener.onEvents(List.of(event));
                                }
                                catch (Exception e)
                                {
                                    listener.onException(e);
                                }
                            }));
        }

        registeredSessionIds.retainAll(
            openSessions
                .stream()
                .map(Session::getId)
                .collect(Collectors.toSet()));
    }
}

//////////////////////////////////////////////////////////////////////////////
