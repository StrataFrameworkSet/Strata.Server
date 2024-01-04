//////////////////////////////////////////////////////////////////////////////
// TestGuiceConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.guice.annotation.EnableGuiceModules;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.server.core.unitofwork.IUnitOfWork;
import strata.server.core.unitofwork.IUnitOfWorkManager;
import strata.server.spring.unitofwork.JpaUnitOfWork;
import strata.server.spring.unitofwork.JpaUnitOfWorkManager;

@Configuration
@EnableGuiceModules
@EnableTransactionManagement
@PropertySource("classpath:repositorytest.properties")
public
class TestGuiceConfiguration
{
    /*
    @Autowired
    private Injector injector;
*/
    @Bean
    public RepositoryTestModule
    repositoryTestModule() { return new RepositoryTestModule(); }

    @Bean
    public IUnitOfWorkManager
    unitOfWorkManager(IUnitOfWork uow)
    {
        return new JpaUnitOfWorkManager((JpaUnitOfWork)uow);
    }
    /*
    @Bean
    public IFooRepository
    fooRepository() { return injector.getInstance(IFooRepository.class); }

     */
}

//////////////////////////////////////////////////////////////////////////////
