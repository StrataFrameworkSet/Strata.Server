//////////////////////////////////////////////////////////////////////////////
// JpaTransactionManagerProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.Provider;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

public
class JpaTransactionManagerProvider
    implements Provider<JpaTransactionManager>
{
    private final EntityManagerFactory factory;

    @Inject
    public
    JpaTransactionManagerProvider(EntityManagerFactory f)
    {
        factory = f;
    }

    @Override
    public JpaTransactionManager
    get()
    {
        JpaTransactionManager manager = new JpaTransactionManager(factory);

        manager.setTransactionSynchronization(
            AbstractPlatformTransactionManager.SYNCHRONIZATION_ALWAYS);
        return manager;
    }
}

//////////////////////////////////////////////////////////////////////////////
