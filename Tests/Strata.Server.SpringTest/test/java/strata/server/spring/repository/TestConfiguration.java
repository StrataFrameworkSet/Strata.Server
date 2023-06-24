//////////////////////////////////////////////////////////////////////////////
// TestConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.Injector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.guice.annotation.EnableGuiceModules;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.server.core.repository.IFooRepository;

@Configuration
@EnableGuiceModules
@EnableTransactionManagement
@PropertySource("classpath:repositorytest.properties")
public
class TestConfiguration
{
    /*
    @Autowired
    private Injector injector;
*/
    @Bean
    public RepositoryTestModule
    repositoryTestModule() { return new RepositoryTestModule(); }

    /*
    @Bean
    public IFooRepository
    repository() { return injector.getInstance(IFooRepository.class); }

     */
}

//////////////////////////////////////////////////////////////////////////////
