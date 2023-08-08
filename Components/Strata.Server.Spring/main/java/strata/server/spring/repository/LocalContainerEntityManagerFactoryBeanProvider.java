//////////////////////////////////////////////////////////////////////////////
// EntityManagerProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.Provider;
import jakarta.inject.Inject;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import strata.foundation.core.configuration.IConfiguration;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

public
class LocalContainerEntityManagerFactoryBeanProvider
    implements Provider<LocalContainerEntityManagerFactoryBean>
{
    private final LocalContainerEntityManagerFactoryBean factory;
    private final static Set<String> requiredProperties = getRequiredProperties();

    @Inject
    public
    LocalContainerEntityManagerFactoryBeanProvider(IConfiguration configuration)
    {
        validateConfiguration(configuration);

        factory =
            configureFactory(
                configuration,
                configureAdapter(
                    configuration,
                    new HibernateJpaVendorAdapter()),
                new LocalContainerEntityManagerFactoryBean());

        factory.afterPropertiesSet();
    }

    @Override
    public LocalContainerEntityManagerFactoryBean
    get()
    {
        return factory;
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

    protected HibernateJpaVendorAdapter
    configureAdapter(
        IConfiguration            configuration,
        HibernateJpaVendorAdapter adapter)
    {
        adapter.setDatabase(Database.DEFAULT);

        if (configuration.hasBooleanProperty("spring.jpa.generate-ddl"))
            adapter.setGenerateDdl(
                configuration.getBooleanProperty("spring.jpa.generate-ddl"));

        if (configuration.hasBooleanProperty("spring.jpa.show-sql"))
            adapter.setShowSql(
                configuration.getBooleanProperty("spring.jpa.show-sql"));

        return adapter;
    }

    protected LocalContainerEntityManagerFactoryBean
    configureFactory(
        IConfiguration                         configuration,
        HibernateJpaVendorAdapter              adapter,
        LocalContainerEntityManagerFactoryBean factory)
    {
        Properties properties = new Properties();

        configuration
            .getProperties("spring.datasource","spring.jpa","hibernate")
            .entrySet()
            .stream()
            .forEach(pair -> properties.put(pair.getKey(),pair.getValue()));

        factory.setJpaProperties(properties);

        factory.setDataSource(getDataSource(configuration));
        factory.setJpaVendorAdapter(adapter);
        factory.setJpaDialect(new HibernateJpaDialect());
        factory.setPersistenceProvider(new HibernatePersistenceProvider());

        if (configuration.hasProperty("spring.jpa.mapping-resources"))
            factory.setMappingResources(
                getMappingResources(
                    configuration.getProperty(
                        "spring.jpa.mapping-resources")));

        if (configuration.hasProperty("spring.jpa.packages-to-scan"))
            factory.setPackagesToScan(
                getPackageToScan(
                    configuration.getProperty(
                        "spring.jpa.packages-to-scan")));

        factory.setPersistenceUnitName(
            configuration.getProperty("jakarta.persistence.persistence-unit"));

        return factory;
    }

    protected void
    validateConfiguration(IConfiguration configuration)
        throws MissingPropertiesException
    {
        List<String> missing =
            requiredProperties
                .stream()
                .filter(property -> !configuration.hasProperty(property))
                .collect(Collectors.toList());

        if (!missing.isEmpty())
            throw new MissingPropertiesException(missing);
    }

    private String[]
    getMappingResources(String list)
    {
        List<String> resources = new ArrayList<>();

        resources.add("Entity.orm.xml");
        resources.addAll(Arrays.asList(list.split(",")));

        return resources.toArray(new String[resources.size()]);
    }

    private String[]
    getPackageToScan(String list)
    {
        return list.split(",");
    }

    private static Set<String>
    getRequiredProperties()
    {
        return
            Set.of(
                "spring.datasource.driver-class-name",
                "spring.datasource.url",
                "spring.datasource.username",
                "spring.datasource.password",
                "jakarta.persistence.persistence-unit");
    }
}

//////////////////////////////////////////////////////////////////////////////
