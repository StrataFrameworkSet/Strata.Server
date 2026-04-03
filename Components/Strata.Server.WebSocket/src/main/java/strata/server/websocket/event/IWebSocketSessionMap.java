/// ///////////////////////////////////////////////////////////////////////////
// IWebSocketSessionMap.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.websocket.event;

import jakarta.websocket.Session;

import java.util.Set;

public
interface IWebSocketSessionMap
{
    IWebSocketSessionMap
    put(String path,Session session);

    IWebSocketSessionMap
    remove(String path,Session session);

    IWebSocketSessionMap
    removeAll(String path);

    IWebSocketSessionMap
    clear();

    Set<Session>
    getAll(String path);

    Set<Session>
    getOpen(String path);

    boolean
    hasAny(String path);

    boolean
    hasOpen(String path);
}

//////////////////////////////////////////////////////////////////////////////