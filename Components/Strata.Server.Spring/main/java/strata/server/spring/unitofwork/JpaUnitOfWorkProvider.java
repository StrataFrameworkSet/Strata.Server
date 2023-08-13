//////////////////////////////////////////////////////////////////////////////
// JpaUnitOfWorkProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.unitofwork;

import com.google.inject.Provider;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import strata.server.core.unitofwork.IUnitOfWork;

public
class JpaUnitOfWorkProvider
    implements Provider<IUnitOfWork>
{
    private EntityManagerFactory factory;

    @Inject
    public
    JpaUnitOfWorkProvider(EntityManagerFactory f)
    {
        factory = f;
    }

    @Override
    public IUnitOfWork
    get()
    {
        return new JpaUnitOfWork(factory);
    }
}

//////////////////////////////////////////////////////////////////////////////
