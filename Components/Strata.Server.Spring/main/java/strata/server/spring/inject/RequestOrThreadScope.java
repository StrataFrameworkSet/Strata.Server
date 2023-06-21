//////////////////////////////////////////////////////////////////////////////
// RequestOrThreadScope.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.inject;


import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import org.springframework.web.context.request.RequestContextHolder;
import strata.foundation.core.inject.ThreadScope;

public
class RequestOrThreadScope
    implements Scope
{
    private final Scope itsImplementation;

    public
    RequestOrThreadScope()
    {
        if (RequestContextHolder.getRequestAttributes() != null)
            itsImplementation = new RequestScope();
        else
            itsImplementation = new ThreadScope();
    }

    @Override
    public <T> Provider<T>
    scope(Key<T> key,Provider<T> provider)
    {
        return itsImplementation.scope(key,provider);
    }
}

//////////////////////////////////////////////////////////////////////////////
