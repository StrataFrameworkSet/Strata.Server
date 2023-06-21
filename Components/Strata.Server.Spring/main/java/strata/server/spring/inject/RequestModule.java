//////////////////////////////////////////////////////////////////////////////
// RequestModule.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.inject;

import com.google.inject.Scopes;
import strata.foundation.core.inject.AbstractModule;

public
class RequestModule
    extends AbstractModule
{
    @Override
    protected void
    configure()
    {
        bind(IRequestProvider.class)
            .to(TransientRequestProvider.class)
            .in(Scopes.SINGLETON);
    }
}

//////////////////////////////////////////////////////////////////////////////
