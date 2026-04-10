/// ///////////////////////////////////////////////////////////////////////////
// HealthCheckConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.health;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import strata.foundation.spring.inject.RequestScoped;

@Configuration
public
class HealthCheckConfiguration
{
    @Bean
    @RequestScoped
    public HealthCheckController
    healthCheckController(EntityManagerFactory factory)
    {
        return new HealthCheckController(factory);
    }
}

//////////////////////////////////////////////////////////////////////////////
