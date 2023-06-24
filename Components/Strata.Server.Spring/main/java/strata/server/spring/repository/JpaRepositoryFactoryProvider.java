//////////////////////////////////////////////////////////////////////////////
// JpaRepositoryFactoryProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.transaction.PlatformTransactionManager;

public
class JpaRepositoryFactoryProvider
    extends AbstractRepositoryFactoryProvider
{
    @Inject
    public
    JpaRepositoryFactoryProvider(EntityManager em,PlatformTransactionManager tm)
    {
        super(em,tm);
    }

    @Override
    protected RepositoryFactorySupport
    getRepositoryFactorySupport(EntityManager em,PlatformTransactionManager tm)
    {
        RepositoryFactorySupport factory = new JpaRepositoryFactory(em);

        return factory;
    }
}

//////////////////////////////////////////////////////////////////////////////
