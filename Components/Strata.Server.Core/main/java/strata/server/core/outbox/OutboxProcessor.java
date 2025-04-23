//////////////////////////////////////////////////////////////////////////////
// OutboxProcessor.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.DebeziumEngine.RecordCommitter;
import io.debezium.engine.format.Json;
import jakarta.inject.Inject;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public
class OutboxProcessor
{
    private final Map<String,IOutboxEventConsumer> consumers;
    private final Properties                       properties;
    private ExecutorService                        executor;
    private IChangeEventMapper                     mapper;

    @Inject
    public
    OutboxProcessor(
        Map<String,IOutboxEventConsumer> consumers,
        Properties                        properties,
        ObjectMapper                      mapper)
    {
        this.consumers = new HashMap<>(consumers);
        this.properties = properties;
        this.mapper = new ChangeEventMapper(mapper);
        executor = Executors.newSingleThreadExecutor();
    }

    public void
    start()
    {
        try (
            DebeziumEngine<ChangeEvent<String,String>> engine =
                DebeziumEngine
                    .create(Json.class)
                    .using(properties)
                    .notifying((events,committer) -> process(events,committer))
                    .build())
        {
            executor.execute(engine);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void
    stop()
    {
        executor.shutdown();
    }

    protected void
    process(
        List<ChangeEvent<String,String>>            events,
        RecordCommitter<ChangeEvent<String,String>> committer)
        throws InterruptedException
    {
        System.out.println("process(events, committer)");
        events.forEach(
            event ->
            {
                try
                {
                    process(event, committer);
                }
                catch (Throwable e)
                {
                    throw new RuntimeException(e);
                }
            });
        committer.markBatchFinished();
    }

    protected void
    process(
        ChangeEvent<String,String>                  event,
        RecordCommitter<ChangeEvent<String,String>> committer)
        throws InterruptedException, JsonProcessingException
    {
        System.out.println("process(" + event + "," + committer + ")");
        System.out.println(event);
        String type = getType(event);

        if (hasConsumer(type))
            routeEvent(type,event,committer);
        else
            notifyUnknownType(type);

    }

    protected String
    getType(ChangeEvent<String,String> event) throws JsonProcessingException
    {
        return
            mapper
                .parse(event)
                .mapEventType();
    }

    protected boolean
    hasConsumer(String type)
    {
        return consumers.containsKey(type);
    }

    protected void
    routeEvent(
        String                                      type,
        ChangeEvent<String,String>                  event,
        RecordCommitter<ChangeEvent<String,String>> committer)
    {
        IOutboxEventConsumer consumer = consumers.get(type);

        consumer.accept(event,committer);
    }

    protected void
    notifyUnknownType(String type)
    {
        System.err.println("notifyUnknownType(" + type + ")");
    }
}

//////////////////////////////////////////////////////////////////////////////
