/// ///////////////////////////////////////////////////////////////////////////
// ChangeEventMapper.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;

import java.io.IOException;
import java.util.*;

public
class ChangeEventMapper
    implements IChangeEventMapper
{
    private final ObjectMapper         mapper;
    private final Map<String,JsonNode> parsed;

    public
    ChangeEventMapper(ObjectMapper mapper)
    {
        this.mapper = mapper;
        this.parsed = new HashMap<>();
    }

    public ChangeEventMapper
    parse(ChangeEvent<String,String> event) throws JsonProcessingException
    {
        String             value = event.value();
        Optional<JsonNode> root = Optional.ofNullable(mapper.readTree(value));
        Optional<JsonNode> envPayload = root.map(r -> r.get("payload"));
        Optional<JsonNode> after = envPayload.map(p -> p.path("after"));
        Optional<JsonNode> id = after.map(a -> a.get("id"));
        Optional<JsonNode> sourceType = after.map(a -> a.get("sourcetype"));
        Optional<JsonNode> sourceId = after.map(a -> a.get("sourceid"));
        Optional<JsonNode> type = after.map(a -> a.get("eventtype"));
        Optional<JsonNode> payload = after.map(a -> a.path("eventpayload"));

        parsed.clear();

        root.orElseThrow(
            () -> new NoSuchElementException("root node not found"));
        envPayload.orElseThrow(
            () -> new NoSuchElementException("envelop payload not found"));
        after.orElseThrow(
            () -> new NoSuchElementException("after not found"));

        parsed.put(
            "id",
            id.orElseThrow(
                () -> new NoSuchElementException("id not found")));
        parsed.put(
            "sourceType",
            sourceType.orElseThrow(
                () -> new NoSuchElementException("sourceType not found")));
        parsed.put(
            "sourceId",
            sourceId.orElseThrow(
                () -> new NoSuchElementException("sourceId not found")));
        parsed.put(
            "eventType",
            type.orElseThrow(
                () -> new NoSuchElementException("eventType not found")));
        parsed.put(
            "eventPayload",
            payload.orElseThrow(
                () -> new NoSuchElementException("eventPayload not found")));

        return this;
    }

    @Override
    public String
    mapId()
    {
        return getPropertyAsString("id");
    }

    @Override
    public String
    mapEventType()
    {
        return getPropertyAsString("eventType");
    }

    @Override
    public <T> T
    mapEventPayload(Class<T> payloadType) throws IOException
    {
        return mapper.readValue(getPropertyAsString("eventPayload"),payloadType);
    }

    protected String
    getPropertyAsString(String property)
    {
        if (parsed.containsKey(property))
            return parsed.get(property).asText();

        throw new NoSuchElementException(property + " not found");
    }
}

//////////////////////////////////////////////////////////////////////////////
