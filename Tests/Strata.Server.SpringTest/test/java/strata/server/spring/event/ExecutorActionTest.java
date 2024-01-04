//////////////////////////////////////////////////////////////////////////////
// ExecutorActionTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.event;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static strata.foundation.core.concurrent.Awaiter.await;

@Tag("CommitStage")
public
class ExecutorActionTest
{
    @BeforeAll
    public static void
    setUp()
    {
        ExecutorServiceAction.setExecutorService(Executors.newSingleThreadExecutor());
    }

    @Test
    public void
    testExecutorServiceActionExecute()
    {
        IExecutorAction          action = new ExecutorServiceAction();
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
        CompletionStage<String>  result1 =
            CompletableFuture.supplyAsync(() -> "XXXXX",action);
        CompletionStage<String>  result2 =
            CompletableFuture.supplyAsync(() -> "YYYY",action);
        CompletionStage<String>  result3 =
            CompletableFuture.supplyAsync(() -> "ZZZ",action);

        executor.schedule(
            () ->
            {
                try
                {
                    action.execute();
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            },
            3,
            TimeUnit.SECONDS);

        await(result1.thenAccept(r -> assertEquals("XXXXX",r)));
        await(result2.thenAccept(r -> assertEquals("YYYY",r)));
        await(result3.thenAccept(r -> assertEquals("ZZZ",r)));
    }

    @Test
    public void
    testExecutorActionExecute()
    {
        IExecutorAction          action = new ExecutorAction();
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
        CompletionStage<String>  result =
            CompletableFuture.supplyAsync(() -> "XXXXX",action);

        executor.schedule(
            () ->
            {
                try
                {
                    action.execute();
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            },
            3,
            TimeUnit.SECONDS);

        await(result.thenAccept(r -> assertEquals("XXXXX",r)));
    }

}

//////////////////////////////////////////////////////////////////////////////
