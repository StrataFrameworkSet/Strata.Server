/// ///////////////////////////////////////////////////////////////////////////
// EmailMessageOutboxEventFactory.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.SerializableAttachment;
import strata.server.core.notification.SerializableEmailMessage;

import java.util.stream.Collectors;

public
class EmailMessageOutboxEventFactory
    implements IEmailMessageOutboxEventFactory
{
    private final ObjectMapper mapper;

    @Inject
    public EmailMessageOutboxEventFactory(ObjectMapper m)
    {
        mapper = m;
    }

    @Override
    public OutboxEvent
    create(IEmailMessage source)
    {
        try
        {
            SerializableEmailMessage message =
                new SerializableEmailMessage()
                    .setSender(source.getSender())
                    .setRecipients(source.getRecipients())
                    .setSubject(source.getSubject())
                    .setContent(source.getContent())
                    .setAttachments(
                        source
                            .getAttachments()
                            .stream()
                            .map(
                                attachment ->
                                    new SerializableAttachment()
                                        .setContentId(attachment.getContentId())
                                        .setContentType(attachment.getContentType())
                                        .setBytes(attachment.getBytes()))
                            .collect(Collectors.toSet()));
            return
                new OutboxEvent()
                    .setSourceId("outbox")
                    .setSourceType(IEmailMessage.class.getSimpleName())
                    .setEventType(IEmailMessage.class.getSimpleName())
                    .setEventPayload(mapper.writeValueAsString(message));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
