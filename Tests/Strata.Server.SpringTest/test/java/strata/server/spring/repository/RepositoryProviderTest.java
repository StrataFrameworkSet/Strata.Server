//////////////////////////////////////////////////////////////////////////////
// EntityManagerProviderTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.guice.annotation.EnableGuiceModules;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import strata.server.core.repository.IFooRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("CommitStage")
@ExtendWith(SpringExtension.class)
@EnableGuiceModules
@ContextConfiguration(classes = {TestConfiguration.class})
public
class RepositoryProviderTest
{
    @Autowired
    private IFooRepository target;

    @BeforeEach
    public void
    setUp() throws Exception
    {
    }

    @AfterEach
    public void
    tearDown()
    {
        target = null;
    }

    @Test
    public void
    testRepositoryInjection() throws Exception
    {
        assertNotNull(target);
    }
}

//////////////////////////////////////////////////////////////////////////////
