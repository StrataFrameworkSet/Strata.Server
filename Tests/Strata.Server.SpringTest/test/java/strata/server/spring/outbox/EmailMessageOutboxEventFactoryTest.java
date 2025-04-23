//////////////////////////////////////////////////////////////////////////////
// EmailMessageOutboxEventFactoryTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import strata.foundation.core.value.EmailAddress;
import strata.server.core.notification.EmailMessageBuilder;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.IEmailMessageBuilder;
import strata.server.core.notification.StandardTemplateRepository;
import strata.server.core.outbox.IEmailMessageOutboxEventFactory;
import strata.server.core.outbox.OutboxEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("CommitStage")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public
class EmailMessageOutboxEventFactoryTest
{
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IEmailMessageOutboxEventFactory target;

    private IEmailMessage message;

    @BeforeEach
    public void
    setUp()
    {
        IEmailMessageBuilder builder =
            new EmailMessageBuilder(new StandardTemplateRepository());

        message =
            builder
                .setSender(new EmailAddress("john.doe@xyz.com"))
                .setSubject("Test Email")
                .addRecipient(new EmailAddress("jane.doe@abc.com"))
                .setContent("This is a test.")
                .build();
    }
    @Test
    public void
    testCreate()
    {
        OutboxEvent expected =
            new OutboxEvent()
                .setSourceType(IEmailMessage.class.getSimpleName())
                .setSourceId("")
                .setEventType(IEmailMessage.class.getSimpleName())
                .setEventPayload("{\"sender\":{\"emailAddress\":\"john.doe@xyz.com\"},\"recipients\":[{\"emailAddress\":\"jane.doe@abc.com\"}],\"subject\":\"Test Email\",\"content\":\"This is a test.\",\"attachments\":[]}");
        OutboxEvent actual = target.create(message);

        assertEquals(
            expected.getSourceType(),
            actual.getSourceType());

        assertEquals(
            expected.getEventType(),
            actual.getEventType());

        assertEquals(
            expected.getEventPayload(),
            actual.getEventPayload());

    }
}

//////////////////////////////////////////////////////////////////////////////
