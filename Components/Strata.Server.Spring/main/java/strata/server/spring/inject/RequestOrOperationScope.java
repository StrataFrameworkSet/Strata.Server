//////////////////////////////////////////////////////////////////////////////
// RequestOrOperationScope.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.inject;


import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import org.springframework.web.context.request.RequestContextHolder;
import strata.foundation.core.inject.OperationScope;

public
class RequestOrOperationScope
    implements Scope
{
    private final Scope itsImplementation;

    public
    RequestOrOperationScope()
    {
        if (RequestContextHolder.getRequestAttributes() != null)
            itsImplementation = new RequestScope();
        else
            itsImplementation = new OperationScope();
    }

    @Override
    public <T> Provider<T>
    scope(Key<T> key,Provider<T> provider)
    {
        return itsImplementation.scope(key,provider);
    }
}

//////////////////////////////////////////////////////////////////////////////
