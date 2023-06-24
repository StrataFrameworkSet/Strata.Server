//////////////////////////////////////////////////////////////////////////////
// EntityManagerProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.Provider;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public
class EntityManagerProvider
    implements Provider<EntityManager>
{
    private final EntityManagerFactory factory;

    @Inject
    public
    EntityManagerProvider(EntityManagerFactory f)
    {
        factory = f;
    }

    @Override
    public EntityManager
    get()
    {
        return factory.createEntityManager();
    }
}

//////////////////////////////////////////////////////////////////////////////
