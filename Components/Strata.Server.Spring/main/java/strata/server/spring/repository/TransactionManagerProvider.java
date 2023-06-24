//////////////////////////////////////////////////////////////////////////////
// TransactionManagerProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.Provider;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

public
class TransactionManagerProvider
    implements Provider<PlatformTransactionManager>
{
    private final EntityManagerFactory factory;

    @Inject
    public
    TransactionManagerProvider(EntityManagerFactory f)
    {
        factory = f;
    }

    @Override
    public PlatformTransactionManager
    get()
    {
        return new JpaTransactionManager(factory);
    }
}

//////////////////////////////////////////////////////////////////////////////
