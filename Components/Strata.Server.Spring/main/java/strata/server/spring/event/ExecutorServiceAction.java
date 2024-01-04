//////////////////////////////////////////////////////////////////////////////
// BasicExecutor.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.event;

import strata.foundation.core.exception.MultiCauseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public
class ExecutorServiceAction
    implements IExecutorAction
{
    private Queue<Runnable>        commands;
    private static ExecutorService service = Executors.newFixedThreadPool(16);

    public
    ExecutorServiceAction()
    {
        commands = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void
    execute(Runnable command)
    {
        commands.add(command);
    }

    @Override
    public void
    execute() throws Exception
    {
        List<Throwable> causes = new ArrayList<>();

        commands
            .stream()
            .forEach(
                command ->
                {
                    try
                    {
                        service.execute(command);
                    }
                    catch (Throwable cause)
                    {
                        causes.add(cause);
                    }
                });

        commands.clear();

        if (!causes.isEmpty())
            throw new MultiCauseException(causes);
    }

    public static void
    setExecutorService(ExecutorService svc)
    {
        service = svc;
    }

}

//////////////////////////////////////////////////////////////////////////////
