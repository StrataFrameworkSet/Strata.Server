//////////////////////////////////////////////////////////////////////////////
// EntityManagerProviderTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.Guice;
import com.google.inject.Injector;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import strata.foundation.core.configuration.IConfiguration;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("CommitStage")
@EnableJpaRepositories
public
class EntityManagerProviderTest
{
    private Injector      injector;
    private EntityManager target;

    @BeforeEach
    public void
    setUp() throws Exception
    {
        injector = Guice.createInjector(new RepositoryTestModule());
        //target   = injector.getInstance(EntityManager.class);
    }

    @AfterEach
    public void
    tearDown()
    {
        if (target != null)
            target.close();

        target = null;
        injector = null;
    }

    @Test
    public void
    testConfiguration()
    {
        IConfiguration configuration = injector.getInstance(IConfiguration.class);

        assertNotNull(configuration);
        assertTrue(configuration.hasProperty("spring.datasource.driver-class-name"));
        assertTrue(configuration.hasProperty("spring.datasource.username"));
        assertTrue(configuration.hasProperty("spring.datasource.password"));
        assertTrue(configuration.hasProperty("spring.datasource.url"));
        assertTrue(configuration.hasProperty("spring.entitymanager.persistence-xml-location"));
    }

    @Test
    public void
    testEntityManager() throws Exception
    {
        target = injector.getInstance(EntityManager.class);

        assertNotNull(target);
    }

    @Test
    public void
    testEntityManagerFactory()
    {
        IConfiguration configuration = injector.getInstance(IConfiguration.class);
        LocalContainerEntityManagerFactoryBean factory =
            new LocalContainerEntityManagerFactoryBean();

        factory.setPersistenceXmlLocation(
            configuration.getProperty(
                "spring.entitymanager.persistence-xml-location"));
        factory.setJtaDataSource(getDataSource(configuration));
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setPersistenceXmlLocation(
            configuration.getProperty(
                "spring.entitymanager.persistence-xml-location"));
        factory.afterPropertiesSet();
        assertNotNull(factory.getDataSource());
        assertNotNull(factory.getObject());
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
