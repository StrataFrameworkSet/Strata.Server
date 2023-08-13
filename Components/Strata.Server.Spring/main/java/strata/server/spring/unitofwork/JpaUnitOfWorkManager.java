//////////////////////////////////////////////////////////////////////////////
// UnitOfWorkTransactionManager.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.unitofwork;

import jakarta.inject.Inject;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import strata.server.core.unitofwork.IUnitOfWorkManager;

public
class JpaUnitOfWorkManager
    extends AbstractPlatformTransactionManager
    implements IUnitOfWorkManager
{
    private JpaUnitOfWork unitOfWork;

    @Inject
    public
    JpaUnitOfWorkManager(JpaUnitOfWork uow)
    {
        unitOfWork = uow;
    }

    @Override
    protected JpaUnitOfWork
    doGetTransaction() throws TransactionException
    {
        return unitOfWork;
    }

    @Override
    protected void
    doBegin(Object transaction,TransactionDefinition definition) throws TransactionException
    {
        if (transaction instanceof JpaUnitOfWork uow)
            uow.begin();
    }

    @Override
    protected void
    doCommit(DefaultTransactionStatus status) throws TransactionException
    {
        if (status.getTransaction() instanceof JpaUnitOfWork uow)
            uow.commit();
    }

    @Override
    protected void
    doRollback(DefaultTransactionStatus status) throws TransactionException
    {
        if (status.getTransaction() instanceof JpaUnitOfWork uow)
            uow.rollback();
    }
}

//////////////////////////////////////////////////////////////////////////////
