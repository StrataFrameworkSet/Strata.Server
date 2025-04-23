//////////////////////////////////////////////////////////////////////////////
// SerializableEmailMessage.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import strata.foundation.core.value.EmailAddress;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public
class SerializableEmailMessage
    implements Serializable, IEmailMessage
{
    private EmailAddress sender;
    private Set<EmailAddress> recipients;
    private String subject;
    private String content;
    private Set<SerializableAttachment> attachments;

    public SerializableEmailMessage()
    {
        sender = null;
        recipients = null;
        subject = null;
        content = null;
        attachments = Set.of();
    }

    public SerializableEmailMessage
    setSender(EmailAddress sender)
    {
        this.sender = sender;
        return this;
    }

    public SerializableEmailMessage
    setRecipients(Set<EmailAddress> recipients)
    {
        this.recipients = new HashSet<>(recipients);
        return this;
    }

    public SerializableEmailMessage
    setSubject(String subject)
    {
        this.subject = subject;
        return this;
    }

    public SerializableEmailMessage
    setContent(String content)
    {
        this.content = content;
        return this;
    }

    public SerializableEmailMessage
    setAttachments(Set<SerializableAttachment> attachments)
    {
        this.attachments = new HashSet<>(attachments);
        return this;
    }

    @Override

    public EmailAddress
    getSender()
    {
        return sender;
    }

    @Override
    public Set<EmailAddress>
    getRecipients()
    {
        return recipients;
    }

    @Override
    public String
    getSubject()
    {
        return subject;
    }

    @Override
    public String
    getContent()
    {
        return content;
    }

    @Override
    public List<IAttachment>
    getAttachments()
    {
        return
            attachments
                .stream()
                .collect(Collectors.toList());
    }
}

//////////////////////////////////////////////////////////////////////////////
