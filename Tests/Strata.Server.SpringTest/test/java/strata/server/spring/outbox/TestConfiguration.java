//////////////////////////////////////////////////////////////////////////////
// TestConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.core.inject.ApplicationConfigurationProvider;
import strata.foundation.spring.mapper.StrataObjectMapperProvider;
import strata.server.core.outbox.*;
import strata.server.core.outbox.IOutboxEventRepository;
import strata.server.core.outbox.OutboxEventRepository;
import strata.server.core.unitofwork.IUnitOfWork;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;
import strata.server.spring.repository.JpaRepositoryFactoryProvider;
import strata.server.spring.repository.JpaTransactionManagerProvider;
import strata.server.spring.repository.LocalContainerEntityManagerFactoryBeanProvider;
import strata.server.spring.unitofwork.JpaUnitOfWork;
import strata.server.spring.unitofwork.JpaUnitOfWorkManager;
import strata.server.spring.unitofwork.SpringUnitOfWorkSynchronizationManager;

@Configuration
@EnableTransactionManagement
public
class TestConfiguration
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
    public JpaUnitOfWorkManager
    unitOfWorkManager(IUnitOfWork unitOfWork)
    {
        return new JpaUnitOfWorkManager((JpaUnitOfWork)unitOfWork);
    }

    @Bean
    public RepositoryFactorySupport
    repositoryFactory(EntityManager entityManager)
    {
        return
            new JpaRepositoryFactoryProvider(entityManager)
                .get();
    }

    @Bean
    public IUnitOfWork
    unitOfWork(EntityManagerFactory factory)
    {
        return new JpaUnitOfWork(factory);
    }

    @Bean
    public IEmailMessageOutboxEventFactory
    emailMessageOutboxActionFactory(ObjectMapper mapper)
    {
        return new EmailMessageOutboxEventFactory(mapper);
    }

    @Bean
    public IOutboxEventRepository
    outboxActionRepository(IUnitOfWork unitOfWork)
    {
        return new OutboxEventRepository(unitOfWork);
    }

    @Bean
    public IUnitOfWorkSynchronizationManager
    synchronizer()
    {
        return new SpringUnitOfWorkSynchronizationManager();
    }

    @Bean
    @Scope("singleton")
    public ObjectMapper
    objectMapper(SpringDocConfigProperties properties)
    {
        HttpMessageNotReadableException resolver;
        return new StrataObjectMapperProvider(properties).jsonMapper();
    }

    @Bean
    @Scope("singleton")
    public SpringDocConfigProperties
    springDocConfigProperties()
    {
        return new SpringDocConfigProperties();
    }

}

//////////////////////////////////////////////////////////////////////////////
