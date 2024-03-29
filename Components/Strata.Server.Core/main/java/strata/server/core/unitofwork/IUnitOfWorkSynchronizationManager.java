//////////////////////////////////////////////////////////////////////////////
// IUnitOfWorkSynchronizationManager.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.unitofwork;

import strata.foundation.core.action.IAction;

public
interface IUnitOfWorkSynchronizationManager
{
    IUnitOfWorkSynchronizationManager
    executeAfterCommit(IAction action);

    IUnitOfWorkSynchronizationManager
    executeAfterRollback(IAction action);
}

//////////////////////////////////////////////////////////////////////////////