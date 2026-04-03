//////////////////////////////////////////////////////////////////////////////
// AbstractUnitOfWorkSynchronizationManager.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.unitofwork;

import strata.foundation.core.action.IAction;

import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public
class AbstractUnitOfWorkSynchronizationManager
    implements IUnitOfWorkSynchronizationManager
{
    private final Queue<IAction> afterCommitActions;
    private final Deque<IAction> afterRollbackActions;

    protected
    AbstractUnitOfWorkSynchronizationManager()
    {
        afterCommitActions   = new ConcurrentLinkedDeque<>();
        afterRollbackActions = new ConcurrentLinkedDeque<>();
    }

    @Override
    public IUnitOfWorkSynchronizationManager
    executeAfterCommit(IAction action)
    {
        afterCommitActions.add(action);
        return this;
    }

    @Override
    public IUnitOfWorkSynchronizationManager
    executeAfterRollback(IAction action)
    {
        afterRollbackActions.addFirst(action);
        return this;
    }

    protected Queue<IAction>
    getAfterCommitActions() { return afterCommitActions; }

    protected Deque<IAction>
    getAfterRollbackActions() { return afterRollbackActions; }

    protected void
    clearAfterCommitActions() { afterCommitActions.clear(); }

    protected void
    clearAfterRollbackActions() { afterRollbackActions.clear(); }
}

//////////////////////////////////////////////////////////////////////////////
