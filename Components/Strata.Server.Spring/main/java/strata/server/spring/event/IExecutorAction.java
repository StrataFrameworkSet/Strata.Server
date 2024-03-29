//////////////////////////////////////////////////////////////////////////////
// IExecutorAction.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.event;

import strata.foundation.core.action.IAction;

import java.util.concurrent.Executor;

public
interface IExecutorAction
    extends Executor, IAction {}

//////////////////////////////////////////////////////////////////////////////