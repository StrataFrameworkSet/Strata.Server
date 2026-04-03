//////////////////////////////////////////////////////////////////////////////
// SingletonScopeRepositoryConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.foundation.core.configuration.IConfiguration;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;
import strata.server.spring.unitofwork.ISpringUnitOfWorkManager;
import strata.server.spring.unitofwork.JpaUnitOfWork;
import strata.server.spring.unitofwork.JpaUnitOfWorkManager;
import strata.server.spring.unitofwork.SpringUnitOfWorkSynchronizationManager;

@Configuration
@EnableTransactionManagement
public
class SingletonScopeRepositoryConfiguration
{
    @Bean
    @Scope("singleton")
    public LocalContainerEntityManagerFactoryBean
    localContainerEntityManagerFactoryBean(IConfiguration configuration)
    {
        return
            new LocalContainerEntityManagerFactoryBeanProvider(configuration)
                .get();
    }

    @Bean
    @Scope("singleton")
    public ISpringUnitOfWorkManager
    unitOfWorkManager(JpaUnitOfWork unitOfWork)
    {
        return new JpaUnitOfWorkManager(unitOfWork);
    }

    @Bean
    @Scope("singleton")
    public JpaUnitOfWork
    unitOfWork(EntityManagerFactory factory)
    {
        return new JpaUnitOfWork(factory);
    }

    @Bean
    @Scope("singleton")
    public IUnitOfWorkSynchronizationManager
    synchronizer()
    {
        return new SpringUnitOfWorkSynchronizationManager();
    }
}

//////////////////////////////////////////////////////////////////////////////
