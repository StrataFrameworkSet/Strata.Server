//////////////////////////////////////////////////////////////////////////////
// RequestAttributeMap.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.inject;

import org.springframework.web.context.request.RequestAttributes;

import java.util.HashMap;
import java.util.Map;

public
class RequestAttributeMap
    implements RequestAttributes
{
    private final Map<String,Object> implementation;

    public
    RequestAttributeMap()
    {
        implementation = new HashMap<>();
    }

    @Override
    public Object
    getAttribute(String name,int scope)
    {
        if (inScope(scope))
            return implementation.get(name);

        return null;
    }

    @Override
    public void
    setAttribute(String name,Object value,int scope)
    {
        if (inScope(scope))
            implementation.put(name,value);
    }

    @Override
    public void
    removeAttribute(String name,int scope)
    {
        if (inScope(scope))
            implementation.remove(name);
    }

    @Override
    public String[]
    getAttributeNames(int scope)
    {
        if (inScope(scope))
            return
                implementation
                    .keySet()
                    .toArray(String[]::new);

        return null;
    }

    @Override
    public void
    registerDestructionCallback(String name,Runnable callback,int scope)
    {
    }

    @Override
    public Object
    resolveReference(String key)
    {
        return null;
    }

    @Override
    public String
    getSessionId()
    {
        return null;
    }

    @Override
    public Object
    getSessionMutex()
    {
        return null;
    }

    private boolean
    inScope(int scope)
    {
        return scope == RequestAttributes.SCOPE_REQUEST;
    }
}

//////////////////////////////////////////////////////////////////////////////
