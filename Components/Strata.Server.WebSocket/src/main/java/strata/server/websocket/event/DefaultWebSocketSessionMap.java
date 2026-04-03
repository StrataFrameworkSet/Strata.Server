/// ///////////////////////////////////////////////////////////////////////////
// DefaultWebSocketSessionMap.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.websocket.event;

import jakarta.websocket.Session;
import strata.foundation.core.collection.IMultiMap;
import strata.foundation.core.collection.SetValuedMultiMap;

import java.util.Set;
import java.util.stream.Collectors;

public
class DefaultWebSocketSessionMap
    implements IWebSocketSessionMap
{
    private final IMultiMap<String,Session> itsSessions;

    public
    DefaultWebSocketSessionMap()
    {
        itsSessions = new SetValuedMultiMap<>();
    }

    @Override
    public IWebSocketSessionMap
    put(String path,Session session)
    {
        itsSessions.put(path,session);
        return this;
    }

    @Override
    public IWebSocketSessionMap
    remove(String path,Session session)
    {
        itsSessions.remove(path,session);
        return this;
    }

    @Override
    public IWebSocketSessionMap
    removeAll(String path)
    {
        itsSessions.remove(path);
        return this;
    }

    @Override
    public IWebSocketSessionMap
    clear()
    {
        itsSessions.clear();
        return this;
    }

    @Override
    public Set<Session>
    getAll(String path)
    {
        return
            itsSessions
                .get(path)
                .stream()
                .map(session -> session.getOpenSessions())
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Session>
    getOpen(String path)
    {
        return
            itsSessions
                .get(path)
                .stream()
                .map(session -> session.getOpenSessions())
                .flatMap(Set::stream)
                .filter(session -> session.isOpen())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean
    hasAny(String path)
    {
        return
            itsSessions
                .get(path)
                .stream()
                .map(session -> session.getOpenSessions())
                .flatMap(Set::stream)
                .collect(Collectors.toSet())
                .size() > 0;
    }

    @Override
    public boolean
    hasOpen(String path)
    {
        return
            itsSessions
                .get(path)
                .stream()
                .map(session -> session.getOpenSessions())
                .flatMap(Set::stream)
                .filter(session -> session.isOpen())
                .collect(Collectors.toSet())
                .size() > 0;
    }
}

//////////////////////////////////////////////////////////////////////////////
