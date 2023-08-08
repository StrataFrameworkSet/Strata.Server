//////////////////////////////////////////////////////////////////////////////
// JpaUnitOfWorkProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.unitofwork;

import com.google.inject.Provider;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import strata.server.core.unitofwork.IUnitOfWork;

public
class JpaUnitOfWorkProvider
    implements Provider<IUnitOfWork>
{
    private JpaTransactionManager transactionManager;
    @Inject
    public JpaUnitOfWorkProvider(JpaTransactionManager manager)
    {
        transactionManager = manager;
    }

    @Override
    public IUnitOfWork
    get()
    {
        return new JpaUnitOfWork(transactionManager);
    }
}

//////////////////////////////////////////////////////////////////////////////
