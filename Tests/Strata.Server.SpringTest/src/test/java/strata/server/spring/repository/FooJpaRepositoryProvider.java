//////////////////////////////////////////////////////////////////////////////
// FooRepositoryProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import jakarta.inject.Inject;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public
class FooJpaRepositoryProvider
    extends AbstractRepositoryProvider<IFooJpaRepository>
{
    @Inject
    protected FooJpaRepositoryProvider(RepositoryFactorySupport factory)
    {
        super(IFooJpaRepository.class,factory);
    }
}

//////////////////////////////////////////////////////////////////////////////
