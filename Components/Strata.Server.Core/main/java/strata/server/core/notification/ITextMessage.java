//////////////////////////////////////////////////////////////////////////////
// ITextMessage.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import strata.foundation.core.value.PhoneNumber;

import java.util.Set;

public
interface ITextMessage
{
    Set<PhoneNumber>
    getRecipients();

    String
    getContent();
}

//////////////////////////////////////////////////////////////////////////////