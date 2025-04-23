//////////////////////////////////////////////////////////////////////////////
// OutboxEvent.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.outbox;

import java.io.Serializable;
import java.util.UUID;

public
class OutboxEvent
    implements Serializable
{
    private UUID   id;
    private String sourceType;
    private String sourceId;
    private String eventType;
    private String eventPayload;

    public
    OutboxEvent()
    {
        id           = null;
        sourceType   = null;
        sourceId     = null;
        eventType    = null;
        eventPayload = null;
    }

    public OutboxEvent
    setId(UUID id)
    {
        this.id = id;
        return this;
    }

    public OutboxEvent
    setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
        return this;
    }

    public OutboxEvent
    setSourceId(String sourceId)
    {
        this.sourceId = sourceId;
        return this;
    }

    public OutboxEvent
    setEventType(String eventType)
    {
        this.eventType = eventType;
        return this;
    }

    public OutboxEvent
    setEventPayload(String eventPayload)
    {
        this.eventPayload = eventPayload;
        return this;
    }

    public UUID
    getId()
    {
        return id;
    }

    public String
    getSourceType()
    {
        return sourceType;
    }

    public String
    getSourceId()
    {
        return sourceId;
    }

    public String
    getEventType()
    {
        return eventType;
    }

    public String
    getEventPayload()
    {
        return eventPayload;
    }

}

//////////////////////////////////////////////////////////////////////////////
