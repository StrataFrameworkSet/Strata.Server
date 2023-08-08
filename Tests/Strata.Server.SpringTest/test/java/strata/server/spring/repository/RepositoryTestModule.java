//////////////////////////////////////////////////////////////////////////////
// RepositoryTestModule.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.internal.SingletonScope;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.core.inject.AbstractModule;
import strata.foundation.core.inject.ApplicationConfigurationProvider;
import strata.foundation.core.inject.ThreadScope;
import strata.server.core.repository.FooBarRepository;
import strata.server.core.repository.IFooBarRepository;
import strata.server.core.repository.IFooRepository;
import strata.server.core.unitofwork.IUnitOfWork;
import strata.server.spring.inject.RequestScope;
import strata.server.spring.unitofwork.JpaUnitOfWorkProvider;

import javax.sql.DataSource;

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
        install(new RepositoryModule());

        bind(IConfiguration.class)
            .toProvider(new ApplicationConfigurationProvider("repositorytest"))
            .in(new SingletonScope());

        bind(IFooRepository.class)
            .toProvider(FooRepositoryProvider.class)
            .in(getDefaultScope());

        bind(IFooBarRepository.class)
            .to(FooBarRepository.class)
            .in(getDefaultScope());

        bind(IFooJpaRepository.class)
            .toProvider(FooJpaRepositoryProvider.class)
            .in(getDefaultScope());
    }
}

//////////////////////////////////////////////////////////////////////////////
