//////////////////////////////////////////////////////////////////////////////
// ReactiveRequestContextFilter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.inject;

import com.google.inject.Injector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public
class ReactiveRequestContextFilter
    implements WebFilter
{
    @Autowired
    private Injector injector;

    @Override
    public Mono<Void>
    filter(ServerWebExchange exchange,WebFilterChain chain)
    {
        return
            chain
                .filter(exchange)
                .contextWrite(
                    context ->
                        context.put(
                            Request.class,
                            new Request(injector)));
    }
}

//////////////////////////////////////////////////////////////////////////////
