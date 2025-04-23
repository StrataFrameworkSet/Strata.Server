/// ///////////////////////////////////////////////////////////////////////////
// AbstractOutboxEventConsumer.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.outbox;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine.RecordCommitter;

public abstract
class AbstractOutboxEventConsumer<T>
    implements IOutboxEventConsumer
{
    @Override
    public void
    accept(
        ChangeEvent<String,String>                  event,
        RecordCommitter<ChangeEvent<String,String>> committer)
        throws RuntimeException
    {
        try
        {
            T payload = map(event);

            process(payload);
            committer.markProcessed(event);
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

    protected abstract T
    map(ChangeEvent<String,String> event);

    protected abstract void
    process(T payload) throws RuntimeException;
}

//////////////////////////////////////////////////////////////////////////////
