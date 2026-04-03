//////////////////////////////////////////////////////////////////////////////
// SimpleEmailMessage.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import strata.foundation.core.value.EmailAddress;

import java.util.List;
import java.util.Set;

public
class SimpleEmailMessage
    extends AbstractEmailMessage
{
    private final String itsContent;

    public
    SimpleEmailMessage(
        EmailAddress      sender,
        Set<EmailAddress> recipients,
        String            subject,
        String            content,
        List<IAttachment> attachments)
    {
        super(sender,recipients,subject,attachments);
        itsContent = content;
    }

    @Override
    public String
    getContent()
    {
        return itsContent;
    }
}

//////////////////////////////////////////////////////////////////////////////
