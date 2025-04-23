/// ///////////////////////////////////////////////////////////////////////////
// IChangeEventMapper.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.debezium.engine.ChangeEvent;

import java.io.IOException;

public
interface IChangeEventMapper
{
    IChangeEventMapper
    parse(ChangeEvent<String,String> event) throws JsonProcessingException;

    String
    mapId() throws JsonProcessingException;

    String
    mapEventType() throws JsonProcessingException;

    <T> T
    mapEventPayload(Class<T> payloadType) throws IOException;
}

//////////////////////////////////////////////////////////////////////////////