//////////////////////////////////////////////////////////////////////////////
// AbstractRepositoryFactoryProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.Provider;
import jakarta.persistence.EntityManager;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.transaction.PlatformTransactionManager;

public abstract
class AbstractRepositoryFactoryProvider
    implements Provider<RepositoryFactorySupport>
{
    private final EntityManager              entityManager;
    private final PlatformTransactionManager transactionManager;

    protected
    AbstractRepositoryFactoryProvider(EntityManager em,PlatformTransactionManager tm)
    {
        entityManager      = em;
        transactionManager = tm;
    }

    @Override
    public RepositoryFactorySupport
    get()
    {
        return getRepositoryFactorySupport(entityManager,transactionManager );
    }

    protected abstract RepositoryFactorySupport
    getRepositoryFactorySupport(EntityManager em,PlatformTransactionManager tm);
}

//////////////////////////////////////////////////////////////////////////////
