//////////////////////////////////////////////////////////////////////////////
// RepositoryTestModule.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.transaction.PlatformTransactionManager;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.core.inject.AbstractModule;
import strata.foundation.core.inject.ApplicationConfigurationProvider;
import strata.foundation.core.inject.ThreadScope;
import strata.server.core.repository.IFooRepository;
import strata.server.spring.inject.RequestScope;

public
class RepositoryTestModule
    extends AbstractModule
{
    public
    RepositoryTestModule()
    {
        setDefaultScope(new ThreadScope());
    }

    @Override
    protected void
    configure()
    {
        super.configure();

        bind(IConfiguration.class)
            .toProvider(new ApplicationConfigurationProvider("repositorytest"))
            .in(getDefaultScope());

        bind(EntityManagerFactory.class)
            .toProvider(EntityManagerFactoryProvider.class)
            .in(getDefaultScope());

        bind(PlatformTransactionManager.class)
            .toProvider(TransactionManagerProvider.class)
            .in(getDefaultScope());

        bind(EntityManager.class)
            .toProvider(EntityManagerProvider.class)
            .in(getDefaultScope());

        bind(RepositoryFactorySupport.class)
            .toProvider(JpaRepositoryFactoryProvider.class)
            .in(getDefaultScope());

        bind(IFooRepository.class)
            .toProvider(FooRepositoryProvider.class)
            .in(getDefaultScope());
    }
}

//////////////////////////////////////////////////////////////////////////////
