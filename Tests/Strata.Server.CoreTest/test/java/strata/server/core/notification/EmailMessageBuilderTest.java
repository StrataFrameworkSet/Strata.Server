//////////////////////////////////////////////////////////////////////////////
// EmailMessageBuilderTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.foundation.core.value.EmailAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("CommitStage")
public
class EmailMessageBuilderTest
{
    private IEmailMessageBuilder itsTarget;

    @BeforeEach
    public void
    setUp()
    {
        ITemplateRepository repository =
            new StandardTemplateRepository()
                .insert(
                    "HelloWorld",
                    "Hello {{name}}, how are you?")
                .insert(
                    "GoodbyeWorld",
                    "Goodbye {{name}}. Talk to you {{time}}.");

        itsTarget = new EmailMessageBuilder(repository);
    }

    @Test
    public void
    testBuildSimple()
    {
        IEmailMessage message =
            itsTarget
                .setSender(new EmailAddress("sender@xyz.com"))
                .addRecipient(new EmailAddress("recipient1@abc.com"))
                .addRecipient(new EmailAddress("recipient2@123.com"))
                .setSubject("Test Message")
                .setTemplateKey("HelloWorld")
                .setContent("This is the content of a test message.")
                .build();

        assertTrue(message instanceof SimpleEmailMessage);
        assertEquals(new EmailAddress("sender@xyz.com"),message.getSender());
        assertEquals(2,message.getRecipients().size());
    }

    @Test
    public void
    testBuildTemplated()
    {
        IEmailMessage message =
            itsTarget
                .setSender(new EmailAddress("sender@xyz.com"))
                .addRecipient(new EmailAddress("recipient1@abc.com"))
                .addRecipient(new EmailAddress("recipient2@123.com"))
                .setSubject("Goodbye Message")
                .setContent("This is the content of a test message.")
                .setTemplateKey("GoodbyeWorld")
                .addParameter("{{name}}","John")
                .addParameter("{{time}}","soon")
                .build();

        assertTrue(message instanceof TemplatedEmailMessage);
        assertEquals(new EmailAddress("sender@xyz.com"),message.getSender());
        assertEquals(2,message.getRecipients().size());
        assertEquals("Goodbye Message",message.getSubject());
        assertEquals(
            "Goodbye John. Talk to you soon.",
            message.getContent()
        );
    }
}

//////////////////////////////////////////////////////////////////////////////
