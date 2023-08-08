//////////////////////////////////////////////////////////////////////////////
// TestSpringConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.core.inject.ApplicationConfigurationProvider;
import strata.server.core.repository.FooBarRepository;
import strata.server.core.repository.FooRepository;
import strata.server.core.repository.IFooBarRepository;
import strata.server.core.repository.IFooRepository;
import strata.server.core.unitofwork.IUnitOfWork;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;
import strata.server.spring.unitofwork.JpaUnitOfWork;
import strata.server.spring.unitofwork.SpringUnitOfWorkSynchronizationManager;

@Configuration
@EnableTransactionManagement
public
class TestSpringConfiguration
{
    @Bean
    public IConfiguration
    configuration()
    {
        return
            new ApplicationConfigurationProvider("repositorytest").get();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean
    localContainerEntityManagerFactoryBean(IConfiguration configuration)
    {
        return
            new LocalContainerEntityManagerFactoryBeanProvider(configuration)
                .get();
    }

    /*
    @Bean
    public EntityManagerFactory
    entityManagerFactory(LocalContainerEntityManagerFactoryBean factory)
    {
        return factory.getObject();
    }
    */
    @Bean
    public EntityManager
    entityManager(EntityManagerFactory entityManagerFactory)
    {
        return entityManagerFactory.createEntityManager();
    }

    @Bean
    public JpaTransactionManager
    transactionManager(EntityManagerFactory entityManagerFactory)
    {
        return new JpaTransactionManagerProvider(entityManagerFactory).get();
    }

    @Bean
    public RepositoryFactorySupport
    repositoryFactory(EntityManager entityManager)
    {
        return
            new JpaRepositoryFactoryProvider(entityManager)
                .get();
    }
    /*
    @Bean
    public IFooRepository
    fooRepository(RepositoryFactorySupport repositoryFactory)
    {
        return repositoryFactory.getRepository(ISpringFooRepository.class);
    }
    */

    @Bean
    public IUnitOfWork
    unitOfWork(JpaTransactionManager transactionManager)
    {
        return new JpaUnitOfWork(transactionManager);
    }

    @Bean
    public IFooRepository
    fooRepository(IUnitOfWork unitOfWork)
    {
        return new FooRepository(unitOfWork);
    }

    @Bean
    public IFooBarRepository
    fooBarRepository(IUnitOfWork unitOfWork)
    {
        return new FooBarRepository(unitOfWork);
    }

    @Bean
    public IFooJpaRepository
    jpaRepository(RepositoryFactorySupport repositoryFactory)
    {
        return repositoryFactory.getRepository(IFooJpaRepository.class);
    }

    @Bean
    public IUnitOfWorkSynchronizationManager
    synchronizer()
    {
        return new SpringUnitOfWorkSynchronizationManager();
    }
}

//////////////////////////////////////////////////////////////////////////////
