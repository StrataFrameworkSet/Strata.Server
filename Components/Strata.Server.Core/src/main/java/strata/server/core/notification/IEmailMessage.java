//////////////////////////////////////////////////////////////////////////////
// IEmailMessage.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import strata.foundation.core.value.EmailAddress;

import java.util.List;
import java.util.Set;

public
interface IEmailMessage
{
    EmailAddress
    getSender();

    Set<EmailAddress>
    getRecipients();

    String
    getSubject();

    String
    getContent();

    List<IAttachment>
    getAttachments();
}

//////////////////////////////////////////////////////////////////////////////