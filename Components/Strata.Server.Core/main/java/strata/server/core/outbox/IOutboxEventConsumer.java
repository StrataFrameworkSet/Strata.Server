/// ///////////////////////////////////////////////////////////////////////////
// IOutboxEventConsumer.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.outbox;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine.RecordCommitter;

import java.util.function.BiConsumer;

public
interface IOutboxEventConsumer
    extends
        BiConsumer<
            ChangeEvent<String,String>,
            RecordCommitter<ChangeEvent<String,String>>>
{
    @Override
    void
    accept(
        ChangeEvent<String,String>                  event,
        RecordCommitter<ChangeEvent<String,String>> committer)
        throws RuntimeException;
}

//////////////////////////////////////////////////////////////////////////////
