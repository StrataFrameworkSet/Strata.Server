//////////////////////////////////////////////////////////////////////////////
// JavaMailMessageSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import jakarta.inject.Inject;
import jakarta.mail.*;
import jakarta.mail.internet.InternetHeaders;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import strata.server.core.inject.IEmailConfigurationProvider;
import strata.foundation.core.action.IActionQueue;
import strata.foundation.core.value.EmailAddress;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public
class JavaMailMessageSender
    implements IEmailMessageSender
{
    private final IEmailConfigurationProvider itsConfiguration;
    private Session itsSession;

    @Inject
    JavaMailMessageSender(IEmailConfigurationProvider configuration)
    {
        itsConfiguration = configuration;
        itsSession = null;
    }

    @Override
    public IEmailMessageSender
    open()
    {
        Properties properties = new Properties();

        properties.putAll(itsConfiguration.get());
        itsSession =
            Session.getInstance(
                properties,
                new Authenticator()
        {
            @Override
            protected PasswordAuthentication
            getPasswordAuthentication()
            {
                Map<String,String> configuration =
                    itsConfiguration.get();
                String user = configuration.get("mail.smtp.user");
                String password = configuration.get("mail.smtp.password");

                return new PasswordAuthentication(user,password);
            }
        });

        return this;
    }

    @Override
    public IEmailMessageSender
    close()
    {
        if (isOpen())
            itsSession = null;

        return this;
    }

    @Override
    public IEmailMessageSender
    send(IEmailMessage message)
    {
        Session       session = getSession();
        MimeMessage   msg = new MimeMessage(session);
        MimeMultipart multi = new MimeMultipart();
        MimeBodyPart  content = new MimeBodyPart();

        try
        {
            msg.setFrom(message.getSender().toString());
            msg.setRecipients(
                Message.RecipientType.TO,
                toString(message.getRecipients()));
            msg.setSubject(message.getSubject());

            content.setText(message.getContent(),"utf-8","html");
            multi.addBodyPart(content);

            message
                .getAttachments()
                .stream()
                .forEach(attachment -> addAttachmentPart(multi,attachment));

            msg.setContent(multi);
            Transport.send(msg);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return this;
    }

    @Override
    public boolean
    isOpen()
    {
        return itsSession != null;
    }

    protected Session
    getSession()
    {
        if (!isOpen())
            throw new IllegalStateException("session not open");

        return itsSession;
    }

    protected String
    toString(Collection<EmailAddress> recipients)
    {
        return
            recipients
                .stream()
                .map( email -> email.toString())
                .collect(Collectors.joining(","));
    }

    protected void
    addAttachmentPart(MimeMultipart multi,IAttachment attachment)
    {
        MimeBodyPart    part = null;
        InternetHeaders headers = new InternetHeaders();

        headers.addHeader("Content-Type", attachment.getContentType());
        headers.addHeader("Content-Transfer-Encoding", "base64");

        try
        {
            part = new MimeBodyPart(headers,attachment.getBytes());
            part.setDisposition(MimeBodyPart.INLINE);
            part.setContentID('<' + attachment.getContentId() + '>');
            part.setFileName(attachment.getFileName());

            multi.addBodyPart(part);
        }
        catch (MessagingException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

}

//////////////////////////////////////////////////////////////////////////////
