//////////////////////////////////////////////////////////////////////////////
// FooRepositoryProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import jakarta.inject.Inject;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import strata.server.core.repository.IFooRepository;

public
class FooRepositoryProvider
    extends AbstractRepositoryProvider<ISpringFooRepository>
{
    @Inject
    protected
    FooRepositoryProvider(RepositoryFactorySupport factory)
    {
        super(ISpringFooRepository.class,factory);
    }
}

//////////////////////////////////////////////////////////////////////////////
