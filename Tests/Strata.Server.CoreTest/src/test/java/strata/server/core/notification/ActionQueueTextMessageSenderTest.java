//////////////////////////////////////////////////////////////////////////////
// ActionQueueTextMessageSenderTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.foundation.core.action.IActionQueue;
import strata.foundation.core.action.StandardActionQueue;
import strata.foundation.core.configuration.SecureConfiguration;
import strata.foundation.core.value.PhoneNumber;
import strata.server.core.inject.SecureTextingConfigurationProvider;

import java.io.InputStream;

@Tag("IntegrationStage")
public
class ActionQueueTextMessageSenderTest
{
    private IActionQueue        itsQueue;
    private ITextMessageSender  itsTarget;
    private ITextMessageBuilder itsBuilder;

    @BeforeEach
    public void
    setUp()
        throws Exception
    {
        itsQueue = new StandardActionQueue();
        itsTarget =
            new ActionQueueTextMessageSender(
                new SecureTextingConfigurationProvider(
                    new SecureConfiguration(getPropertiesFile())),
                itsQueue);
        itsBuilder =
            new TextMessageBuilder(
                new StandardTemplateRepository()
                    .insert(
                        "HelloWorld",
                        "Test Message: Hello {{name}} - {{greeting}}"));
    }

    @Test
    public void
    testSend() throws Exception
    {
        ITextMessage message =
            itsBuilder
                .addRecipient(new PhoneNumber("1-562-480-3895"))
                .addRecipient(new PhoneNumber("1-562-889-0909"))
                .setTemplateKey("HelloWorld")
                .addParameter("{{name}}","John and Turath")
                .addParameter("{{greeting}}","what's happening?")
                .build();

        itsTarget.send(message);

        itsQueue.execute();
    }

    protected InputStream
    getPropertiesFile()
    {
        return
            ClassLoader.getSystemResourceAsStream("test.properties");
    }

}

//////////////////////////////////////////////////////////////////////////////
