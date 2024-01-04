//////////////////////////////////////////////////////////////////////////////
// RepositoryModule.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.internal.SingletonScope;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.core.inject.AbstractModule;
import strata.server.core.unitofwork.IUnitOfWork;
import strata.server.core.unitofwork.IUnitOfWorkManager;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;
import strata.server.spring.unitofwork.ISpringUnitOfWorkManager;
import strata.server.spring.unitofwork.JpaUnitOfWorkManagerProvider;
import strata.server.spring.unitofwork.JpaUnitOfWorkProvider;
import strata.server.spring.unitofwork.SpringUnitOfWorkSynchronizationManager;

public
class RepositoryModule
    extends AbstractModule
{

    @Override
    protected void
    configure()
    {
        requireBinding(IConfiguration.class);

        bind(LocalContainerEntityManagerFactoryBean.class)
            .toProvider(LocalContainerEntityManagerFactoryBeanProvider.class)
            .in(new SingletonScope());

        bind(EntityManagerFactory.class)
            .toProvider(EntityManagerFactoryProvider.class)
            .in(new SingletonScope());

        bind(EntityManager.class)
            .toProvider(EntityManagerProvider.class)
            .in(getDefaultScope());

        bind(RepositoryFactorySupport.class)
            .toProvider(JpaRepositoryFactoryProvider.class)
            .in(getDefaultScope());

        bind(IUnitOfWork.class)
            .toProvider(JpaUnitOfWorkProvider.class)
            .in(getDefaultScope());

        bind(ISpringUnitOfWorkManager.class)
            .toProvider(JpaUnitOfWorkManagerProvider.class)
            .in(getDefaultScope());

        bind(IUnitOfWorkSynchronizationManager.class)
            .to(SpringUnitOfWorkSynchronizationManager.class)
            .in(getDefaultScope());
    }
}

//////////////////////////////////////////////////////////////////////////////
