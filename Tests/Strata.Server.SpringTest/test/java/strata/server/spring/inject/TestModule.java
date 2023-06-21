//////////////////////////////////////////////////////////////////////////////
// TestModule.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.inject;

import com.google.inject.TypeLiteral;
import strata.foundation.core.inject.AbstractModule;

import java.util.ArrayList;
import java.util.List;

public
class TestModule
    extends AbstractModule
{
    @Override
    protected void
    configure()
    {
        install(new RequestModule());

        bind(new TypeLiteral<List<Integer>>() {})
            .to(new TypeLiteral<ArrayList<Integer>>() {})
            .in(new RequestScope());
    }
}

//////////////////////////////////////////////////////////////////////////////
