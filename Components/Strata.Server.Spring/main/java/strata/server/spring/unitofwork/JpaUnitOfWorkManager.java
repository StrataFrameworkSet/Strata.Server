//////////////////////////////////////////////////////////////////////////////
// UnitOfWorkTransactionManager.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.unitofwork;

import jakarta.inject.Inject;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import strata.server.core.unitofwork.IUnitOfWork;

public
class JpaUnitOfWorkManager
    extends AbstractPlatformTransactionManager
    implements ISpringUnitOfWorkManager
{
    private JpaUnitOfWork unitOfWork;

    @Inject
    public
    JpaUnitOfWorkManager(JpaUnitOfWork uow)
    {
        super();

        if (super.logger == null)
            super.logger = LogFactory.getLog(JpaUnitOfWorkManager.class);

        if (super.logger == null)
            throw new NullPointerException("logger did not initialize correctly");

        unitOfWork = uow;
    }

    @Override
    public IUnitOfWork
    getUnitOfWork()
    {
        return unitOfWork;
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
