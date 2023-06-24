//////////////////////////////////////////////////////////////////////////////
// EntityManagerProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.Provider;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import strata.foundation.core.configuration.IConfiguration;

import javax.sql.DataSource;

public
class EntityManagerFactoryProvider
    implements Provider<EntityManagerFactory>
{
    private final LocalContainerEntityManagerFactoryBean factory;

    @Inject
    public EntityManagerFactoryProvider(IConfiguration configuration)
    {
        factory = new LocalContainerEntityManagerFactoryBean();

        if (configuration.hasProperty(
            "spring.entitymanager.persistence-xml-location"))
            factory.setPersistenceXmlLocation(
                configuration.getProperty(
                    "spring.entitymanager.persistence-xml-location"));

        factory.setJtaDataSource(getDataSource(configuration));
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.afterPropertiesSet();
    }

    @Override
    public EntityManagerFactory
    get()
    {
        return factory.getObject();
    }

    protected DataSource
    getDataSource(IConfiguration configuration)
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(
            configuration.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(
            configuration.getProperty("spring.datasource.url"));
        dataSource.setUsername(
            configuration.getProperty("spring.datasource.username"));
        dataSource.setPassword(
            configuration.getProperty("spring.datasource.password"));

        return dataSource;
    }
}

//////////////////////////////////////////////////////////////////////////////
