//////////////////////////////////////////////////////////////////////////////
// AbstractWebSocketServerEventSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.websocket.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.Session;
import strata.foundation.core.event.CompletableSendResult;
import strata.foundation.core.event.ICompletableSendResult;
import strata.foundation.core.event.IEventSender;
import strata.foundation.core.event.SendResult;
import strata.foundation.core.exception.MultiCauseException;
import strata.foundation.core.mapper.ObjectMapperSupplier;

import java.util.concurrent.atomic.AtomicBoolean;

public
class AbstractWebSocketServerEventSender<E>
    implements IEventSender<E>
{
    private final IWebSocketSessionMap sessions;
    private final String               path;
    private final AtomicBoolean        opened;
    private final ObjectMapper         mapper;


    protected
    AbstractWebSocketServerEventSender(IWebSocketSessionMap sessions,String path)
    {
        this.sessions     = sessions;
        this.path         = path;
        this.opened       = new AtomicBoolean(false);
        this.mapper       = new ObjectMapperSupplier().get();
    }

    @Override
    public IEventSender<E>
    open() throws Exception
    {
        if (!sessions.getOpen(path).isEmpty())
            opened.set(true);

        return this;
    }

    @Override
    public IEventSender<E>
    close() throws Exception
    {
        opened.set(false);
        return this;
    }

    @Override
    public ICompletableSendResult<E>
    send(E e)
    {
        try
        {
            String payload = mapper.writeValueAsString(e);

            if (isOpen())
                return
                    sessions
                        .getOpen(path)
                        .stream()
                        .map(s -> doSend(s,e,payload))
                        .reduce(this::combine)
                        .map(result -> CompletableSendResult.completedWith(result))
                        .orElse(
                            CompletableSendResult
                                .completedWith(
                                    new SendResult<>(
                                        new IllegalStateException("No open sessions."))));

            return
                CompletableSendResult
                    .completedWith(
                        new SendResult<>(
                            new IllegalStateException("No open sessions.")));
        }
        catch (Exception ex)
        {
            return CompletableSendResult.completedWith(new SendResult<>(ex));
        }
    }

    @Override
    public boolean
    isOpen()
    {
        return opened.get();
    }

    @Override
    public boolean
    isClosed()
    {
        return !isOpen();
    }

    private SendResult<E>
    doSend(Session s,E event,String payload)
    {
        try
        {
            s.getBasicRemote().sendText(payload);
            return new SendResult<>(event);
        }
        catch (Exception ex)
        {
            return new SendResult<>(ex);
        }
    }

    private SendResult<E>
    combine(SendResult<E> a,SendResult<E> b)
    {
        if (a.isSuccess() && b.isSuccess())
        {
            if (a.getSentEvent().equals(b.getSentEvent()))
                return a;

            return
                new SendResult<>(
                    new IllegalStateException(
                        "Cannot combine send results for different events."));

        }
        else if (!a.isSuccess() && b.isSuccess())
            return a;
        else if (a.isSuccess() && !b.isSuccess())
            return b;

        return
            new SendResult<>(
                new MultiCauseException(
                    a.getException().get(),
                    b.getException().get()));

    }
}

//////////////////////////////////////////////////////////////////////////////
