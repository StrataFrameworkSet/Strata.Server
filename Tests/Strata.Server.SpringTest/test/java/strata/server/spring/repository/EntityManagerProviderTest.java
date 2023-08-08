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
import strata.foundation.core.configuration.IConfiguration;

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
    }

    @Test
    public void
    testEntityManager() throws Exception
    {
        target = injector.getInstance(EntityManager.class);

        assertNotNull(target);
    }
}

//////////////////////////////////////////////////////////////////////////////
