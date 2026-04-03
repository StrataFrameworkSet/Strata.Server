//////////////////////////////////////////////////////////////////////////////
// AbstractEmailMessage.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import strata.foundation.core.value.EmailAddress;

import java.util.*;

public abstract
class AbstractEmailMessage
    implements IEmailMessage
{
    private final EmailAddress      itsSender;
    private final Set<EmailAddress> itsRecipients;
    private final String            itsSubject;
    private final List<IAttachment> itsAttachments;

    protected
    AbstractEmailMessage(
        EmailAddress      sender,
        Set<EmailAddress> recipients,
        String            subject,
        List<IAttachment> attachments)
    {
        if (sender == null)
            throw new NullPointerException("sender must be provided");

        if (recipients == null)
            throw
                new NullPointerException("recipients must be provided");

        if (recipients.isEmpty())
            throw
                new IllegalArgumentException("recipients must be non-empty");

        itsSender     = sender;
        itsRecipients = new HashSet<>(recipients);
        itsSubject    = subject;
        itsAttachments = new ArrayList<>(attachments);
    }

    @Override
    public EmailAddress
    getSender()
    {
        return itsSender;
    }

    @Override
    public Set<EmailAddress>
    getRecipients()
    {
        return Collections.unmodifiableSet(itsRecipients);
    }

    @Override
    public String
    getSubject()
    {
        return itsSubject;
    }

    @Override
    public List<IAttachment>
    getAttachments()
    {
        return Collections.unmodifiableList(itsAttachments);
    }
}

//////////////////////////////////////////////////////////////////////////////
