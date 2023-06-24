//////////////////////////////////////////////////////////////////////////////
// RepositoryModule.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.transaction.PlatformTransactionManager;
import strata.foundation.core.inject.AbstractModule;

public
class RepositoryModule
    extends AbstractModule
{
    @Override
    protected void
    configure()
    {
        bind(EntityManagerFactory.class)
            .toProvider(EntityManagerFactoryProvider.class)
            .in(getDefaultScope());

        bind(EntityManager.class)
            .toProvider(EntityManagerProvider.class)
            .in(getDefaultScope());

        bind(RepositoryFactorySupport.class)
            .toProvider(JpaRepositoryFactoryProvider.class)
            .in(getDefaultScope());

        bind(PlatformTransactionManager.class)
            .to(PlatformTransactionManager.class)
            .in(getDefaultScope());

    }
}

//////////////////////////////////////////////////////////////////////////////
