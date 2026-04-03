//////////////////////////////////////////////////////////////////////////////
// AbstractRepositoryProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.Provider;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public abstract
class AbstractRepositoryProvider<R>
    implements Provider<R>
{
    private final Class<R>                 repositoryType;
    private final RepositoryFactorySupport factory;

    protected
    AbstractRepositoryProvider(Class<R> type,RepositoryFactorySupport f)
    {
        repositoryType = type;
        factory = f;
    }

    @Override
    public R
    get()
    {
        return factory.getRepository(repositoryType);
    }
}

//////////////////////////////////////////////////////////////////////////////
