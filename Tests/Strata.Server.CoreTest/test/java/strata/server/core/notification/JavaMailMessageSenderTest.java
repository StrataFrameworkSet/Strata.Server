//////////////////////////////////////////////////////////////////////////////
// JavaMailMessageSenderTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.server.core.inject.SecureEmailConfigurationProvider;
import strata.foundation.core.action.IActionQueue;
import strata.foundation.core.action.StandardActionQueue;
import strata.foundation.core.configuration.SecureConfiguration;
import strata.foundation.core.value.EmailAddress;

import java.io.InputStream;
import java.util.UUID;

@Tag("IntegrationStage")
public
class JavaMailMessageSenderTest
{
    private IActionQueue         itsQueue;
    private IEmailMessageSender  itsTarget;
    private IEmailMessageBuilder itsBuilder;

    @BeforeEach
    public void
    setUp()
        throws Exception
    {

        itsQueue = new StandardActionQueue();
        itsTarget =
            new JavaMailMessageSender(
                itsQueue,
                new SecureEmailConfigurationProvider(
                    new SecureConfiguration(getPropertiesFile())));
        itsBuilder =
            new EmailMessageBuilder(
                new StandardTemplateRepository()
                    .insert(
                        "HelloWorld",
                        "Hello {{name}} - {{greeting}}")
                    .insert(
                        "HelloHtml",
                        getHtmlTemplate()));
    }

    @Test
    public void
    testSend() throws Exception
    {
        IEmailMessage message =
            itsBuilder
                .setSender(new EmailAddress("ezvaluator@gmail.com"))
                .addRecipient(new EmailAddress("johnliebenau@gmail.com"))
                .setSubject("Email Sender Test")
                .setTemplateKey("HelloWorld")
                .addParameter("{{name}}","John")
                .addParameter("{{greeting}}","what's happening")
                .build();

        itsTarget.send(message);

        itsQueue.execute();
    }

    @Test
    public void
    testSendHtml() throws Exception
    {
        IEmailMessage message =
            itsBuilder
                .setSender(new EmailAddress("ezvaluator@gmail.com"))
                .addRecipient(new EmailAddress("johnliebenau@gmail.com"))
                .addRecipient(new EmailAddress("lama90703@yahoo.com"))
                .setSubject("Email Sender Test")
                .setTemplateKey("HelloHtml")
                .addParameter("{{user}}","John Liebenau")
                .addParameter("{{correlationId}}",UUID.randomUUID().toString())
                .addParameter("{{userId}}",UUID.randomUUID().toString())
                .addParameter("{{userName}}","johnliebenau")
                .addParameter("{{confirmCode}}","235711")
                .addAttachment(
                    new ResourceAttachment("logo","image/png","logo.png"))
                .build();

        itsTarget.send(message);

        itsQueue.execute();
    }

    private String
    getHtmlTemplate()
    {
        StringBuilder builder = new StringBuilder();

        builder
            .append("<html>")
            .append("<div style=\"background-color: lightgray;")
            .append("                        height: 60px;")
            .append("                        vertical-align: middle;")
            .append("                        padding: 10px;\">")
            .append("    <img style=\"display: inline-block;")
            .append("                            height: 60px;")
            .append("                            width: 60px;")
            .append("                            vertical-align: middle;\" src=\"cid:logo\" />")
            .append("    <div style=\"display: inline-block;")
            .append("                            height: 60px;")
            .append("                            line-height: 60px;")
            .append("                            color: white;")
            .append("                            vertical-align: middle;")
            .append("                            font-size: 32pt;")
            .append("                            padding-left: 5px;\">EzValuator</div>")
            .append("</div>")
            .append("<span style=\"font-size: 18pt;color: #242424;\">To complete EzValuator registration for user '{{user}}' please confirm email address by clicking on the link: </span>")
            .append("<a style=\"font-size: 18pt;\" href=\"https://ezvaluatorweb.azurewebsites.net/#/verification?correlationId={{correlationId}}&userId={{userId}}&userName={{userName}}&confirmationCode={{confirmCode}}\">Confirm Email Address</a>")
            .append("")
            .append("</html>");

        return builder.toString();
    }

    protected InputStream
    getPropertiesFile()
    {
        return
            ClassLoader.getSystemResourceAsStream("test.properties");
    }
}

//////////////////////////////////////////////////////////////////////////////
