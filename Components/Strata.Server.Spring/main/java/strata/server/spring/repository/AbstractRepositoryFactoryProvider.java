//////////////////////////////////////////////////////////////////////////////
// AbstractRepositoryFactoryProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.Provider;
import jakarta.persistence.EntityManager;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public abstract
class AbstractRepositoryFactoryProvider
    implements Provider<RepositoryFactorySupport>
{
    private final EntityManager entityManager;

    protected
    AbstractRepositoryFactoryProvider(EntityManager em)
    {
        entityManager = em;
    }

    @Override
    public RepositoryFactorySupport
    get()
    {
        return getRepositoryFactorySupport(entityManager);
    }

    protected abstract RepositoryFactorySupport
    getRepositoryFactorySupport(EntityManager em);
}

//////////////////////////////////////////////////////////////////////////////
