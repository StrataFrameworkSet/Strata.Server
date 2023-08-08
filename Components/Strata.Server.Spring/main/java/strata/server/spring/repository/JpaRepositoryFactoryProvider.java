//////////////////////////////////////////////////////////////////////////////
// JpaRepositoryFactoryProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public
class JpaRepositoryFactoryProvider
    extends AbstractRepositoryFactoryProvider
{
    @Inject
    public
    JpaRepositoryFactoryProvider(EntityManager em)
    {
        super(em);
    }

    @Override
    protected RepositoryFactorySupport
    getRepositoryFactorySupport(EntityManager em)
    {
        return new JpaRepositoryFactory(em);
    }
}

//////////////////////////////////////////////////////////////////////////////
