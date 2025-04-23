//////////////////////////////////////////////////////////////////////////////
// EmailMessageOutboxEventConsumer.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.IEmailMessageSender;
import strata.server.core.notification.SerializableEmailMessage;

import java.io.IOException;

public
class EmailMessageOutboxEventConsumer
    extends AbstractOutboxEventConsumer<IEmailMessage>
{
    private final IChangeEventMapper  mapper;
    private final IEmailMessageSender sender;

    public EmailMessageOutboxEventConsumer(
        ObjectMapper        mapper,
        IEmailMessageSender sender)
    {
        super();
        this.mapper = new ChangeEventMapper(mapper);
        this.sender = sender;
    }

    @Override
    protected IEmailMessage
    map(ChangeEvent<String,String> event)
    {

        try
        {
            SerializableEmailMessage message =
                mapper
                .parse(event)
                .mapEventPayload(SerializableEmailMessage.class);

            return message;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void
    process(IEmailMessage payload)
        throws RuntimeException
    {
        sender.send(payload);
    }
}

//////////////////////////////////////////////////////////////////////////////
