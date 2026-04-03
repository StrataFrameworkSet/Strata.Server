//////////////////////////////////////////////////////////////////////////////
// ServiceConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.service;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public
class ServiceConfiguration
{
    @Bean
    public HttpMessageConverters
    httpMessageConverters()
    {
        return
            new HttpMessageConverters(
                new ServiceReplyHttpMessageConverter());
    }
}

//////////////////////////////////////////////////////////////////////////////
