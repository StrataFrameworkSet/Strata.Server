//////////////////////////////////////////////////////////////////////////////
// EmailMessageBuilderTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.foundation.core.value.PhoneNumber;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("CommitStage")
public
class TextMessageBuilderTest
{
    private ITextMessageBuilder itsTarget;

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

        itsTarget = new TextMessageBuilder(repository);
    }

    @Test
    public void
    testBuildSimple()
    {
        ITextMessage message =
            itsTarget
                .addRecipient(new PhoneNumber("123-555-1234"))
                .addRecipient(new PhoneNumber("234-555-2345"))
                .setTemplateKey("HelloWorld")
                .setContent("This is the content of a test message.")
                .build();

        assertTrue(message instanceof SimpleTextMessage);
        assertEquals(2,message.getRecipients().size());
    }

    @Test
    public void
    testBuildTemplated()
    {
        ITextMessage message =
            itsTarget
                .addRecipient(new PhoneNumber("123-555-1234"))
                .addRecipient(new PhoneNumber("234-555-2345"))
                .setContent("This is the content of a test message.")
                .setTemplateKey("GoodbyeWorld")
                .addParameter("{{name}}","John")
                .addParameter("{{time}}","soon")
                .build();

        assertTrue(message instanceof TemplatedTextMessage);
        assertEquals(2,message.getRecipients().size());
        assertEquals(
            "Goodbye John. Talk to you soon.",
            message.getContent()
        );
    }
}

//////////////////////////////////////////////////////////////////////////////
