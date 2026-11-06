//////////////////////////////////////////////////////////////////////////////
// LocalContainerEntityManagerFactoryBeanProviderTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.core.configuration.SecureConfiguration;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@Tag("CommitStage")
public
class LocalContainerEntityManagerFactoryBeanProviderTest
{
    private static final String BASE_PROPERTIES =
        """
        jakarta.persistence.persistence-unit=hikari-test
        spring.datasource.driver-class-name=org.h2.Driver
        spring.datasource.username=sa
        spring.datasource.password=password
        spring.datasource.url=jdbc:h2:mem:strata-server-hikari;DB_CLOSE_DELAY=-1
        hibernate.dialect=org.hibernate.dialect.H2Dialect
        spring.jpa.mapping-resources=Foo.orm.xml,FooBar.orm.xml
        """;

    private static final String POOL_PROPERTIES =
        """
        spring.datasource.hikari.maximum-pool-size=7
        spring.datasource.hikari.minimum-idle=2
        spring.datasource.hikari.connection-timeout=12000
        spring.datasource.hikari.idle-timeout=240000
        spring.datasource.hikari.max-lifetime=900000
        spring.datasource.hikari.pool-name=strata-test-pool
        """;

    private LocalContainerEntityManagerFactoryBeanProvider target;
    private List<HikariDataSource>                         dataSources;

    @BeforeEach
    public void
    setUp() throws Exception
    {
        target      = new LocalContainerEntityManagerFactoryBeanProvider(createConfiguration(BASE_PROPERTIES));
        dataSources = new ArrayList<>();
    }

    @AfterEach
    public void
    tearDown()
    {
        dataSources.forEach(HikariDataSource::close);
        target.get().destroy();

        if (target.get().getDataSource() instanceof HikariDataSource dataSource)
            dataSource.close();

        dataSources = null;
        target      = null;
    }

    @Test
    public void
    testFactoryUsesHikariDataSource()
    {
        assertInstanceOf(HikariDataSource.class,target.get().getDataSource());
    }

    @Test
    public void
    testGetDataSourceWithPoolSettings() throws Exception
    {
        HikariDataSource dataSource =
            getDataSource(createConfiguration(BASE_PROPERTIES + POOL_PROPERTIES));

        assertEquals("org.h2.Driver",dataSource.getDriverClassName());
        assertEquals("jdbc:h2:mem:strata-server-hikari;DB_CLOSE_DELAY=-1",dataSource.getJdbcUrl());
        assertEquals("sa",dataSource.getUsername());
        assertEquals("password",dataSource.getPassword());
        assertEquals(7,dataSource.getMaximumPoolSize());
        assertEquals(2,dataSource.getMinimumIdle());
        assertEquals(12000L,dataSource.getConnectionTimeout());
        assertEquals(240000L,dataSource.getIdleTimeout());
        assertEquals(900000L,dataSource.getMaxLifetime());
        assertEquals("strata-test-pool",dataSource.getPoolName());
    }

    @Test
    public void
    testGetDataSourceWithHikariDefaults() throws Exception
    {
        HikariDataSource dataSource = getDataSource(createConfiguration(BASE_PROPERTIES));
        HikariConfig     defaults   = new HikariConfig();

        defaults.setJdbcUrl(dataSource.getJdbcUrl());
        defaults.validate();

        assertEquals("jdbc:h2:mem:strata-server-hikari;DB_CLOSE_DELAY=-1",dataSource.getJdbcUrl());
        assertEquals("sa",dataSource.getUsername());
        assertEquals(defaults.getMaximumPoolSize(),dataSource.getMaximumPoolSize());
        assertEquals(defaults.getMaximumPoolSize(),dataSource.getMinimumIdle());
        assertEquals(defaults.getConnectionTimeout(),dataSource.getConnectionTimeout());
        assertEquals(defaults.getIdleTimeout(),dataSource.getIdleTimeout());
        assertEquals(defaults.getMaxLifetime(),dataSource.getMaxLifetime());
    }

    private HikariDataSource
    getDataSource(IConfiguration configuration)
    {
        DataSource       dataSource = target.getDataSource(configuration);
        HikariDataSource hikari     = assertInstanceOf(HikariDataSource.class,dataSource);

        dataSources.add(hikari);

        return hikari;
    }

    private IConfiguration
    createConfiguration(String properties) throws IOException
    {
        return
            new SecureConfiguration(
                new ByteArrayInputStream(
                    properties.getBytes(StandardCharsets.UTF_8)));
    }
}

//////////////////////////////////////////////////////////////////////////////
