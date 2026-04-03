//////////////////////////////////////////////////////////////////////////////
// ExecutorAction.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.event;

import java.util.Optional;

public
class ExecutorAction
    implements IExecutorAction
{
    private Optional<Runnable> command;

    public
    ExecutorAction()
    {
        command = Optional.empty();

    }
    @Override
    public void
    execute(Runnable cmd)
    {
        command = Optional.of(cmd);
    }

    @Override
    public void
    execute() throws Exception
    {
        command
            .get()
            .run();
    }
}

//////////////////////////////////////////////////////////////////////////////
