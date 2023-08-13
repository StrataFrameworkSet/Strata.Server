//////////////////////////////////////////////////////////////////////////////
// SpringUnitOfWorkSynchronizationManager.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.unitofwork;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import strata.foundation.core.action.IAction;
import strata.foundation.core.exception.MultiCauseException;
import strata.server.core.unitofwork.AbstractUnitOfWorkSynchronizationManager;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public
class SpringUnitOfWorkSynchronizationManager
    extends AbstractUnitOfWorkSynchronizationManager
    implements TransactionSynchronization
{
    private final AtomicBoolean registered;
    public
    SpringUnitOfWorkSynchronizationManager()
    {
        registered = new AtomicBoolean(false);
    }

    @Override
    public SpringUnitOfWorkSynchronizationManager
    executeAfterCommit(IAction action)
    {
        super.executeAfterCommit(action);
        register();
        return this;
    }

    @Override
    public SpringUnitOfWorkSynchronizationManager
    executeAfterRollback(IAction action)
    {
        super.executeAfterRollback(action);
        register();
        return this;
    }

    @Override
    public void
    afterCommit()
    {
        try
        {
            doAfterCommit();
        }
        finally
        {
            clearAfterCommitActions();
            clearAfterRollbackActions();
        }
    }

    @Override
    public void
    afterCompletion(int status)
    {
        registered.set(false);

        try
        {
            switch (status)
            {
                case STATUS_COMMITTED:
                    doAfterCommit();
                    break;

                case STATUS_ROLLED_BACK:
                    doAfterRollback();
                    break;

                default:
                    break;
            }
        }
        finally
        {
            clearAfterCommitActions();
            clearAfterRollbackActions();
        }
    }

    protected void
    register()
    {
        if (registered.get() == false)
        {
            if (!TransactionSynchronizationManager.isSynchronizationActive())
                TransactionSynchronizationManager.initSynchronization();

            TransactionSynchronizationManager.registerSynchronization(this);
            registered.set(true);
        }
    }
    protected void
    doAfterCommit()
        throws MultiCauseException
    {
        Queue<IAction>  actions = getAfterCommitActions();
        List<Throwable> causes  = new ArrayList<>();

        while (!actions.isEmpty())
        {
            try
            {
                actions
                    .remove()
                    .execute();
            }
            catch (Throwable cause)
            {
                causes.add(cause);
            }
        }

        if (!causes.isEmpty())
            throw new MultiCauseException(causes);
    }

    protected void
    doAfterRollback()
        throws MultiCauseException
    {
        Deque<IAction>  actions = getAfterRollbackActions();
        List<Throwable> causes  = new ArrayList<>();

        while (!actions.isEmpty())
        {
            try
            {
                actions
                    .removeFirst()
                    .execute();
            }
            catch (Throwable cause)
            {
                causes.add(cause);
            }
        }

        if (!causes.isEmpty())
            throw new MultiCauseException(causes);

    }
}

//////////////////////////////////////////////////////////////////////////////
