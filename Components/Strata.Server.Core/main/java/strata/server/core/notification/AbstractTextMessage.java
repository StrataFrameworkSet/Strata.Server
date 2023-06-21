//////////////////////////////////////////////////////////////////////////////
// AbstractTextMessage.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import strata.foundation.core.value.PhoneNumber;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract
class AbstractTextMessage
    implements ITextMessage
{
    private final Set<PhoneNumber> itsRecipients;

    protected
    AbstractTextMessage(Set<PhoneNumber> recipients)
    {
        if (recipients == null)
            throw
                new NullPointerException("recipients must be provided");

        if (recipients.isEmpty())
            throw
                new IllegalArgumentException("recipients must be non-empty");

        itsRecipients = new HashSet<>(recipients);
    }

    @Override
    public Set<PhoneNumber>
    getRecipients()
    {
        return Collections.unmodifiableSet(itsRecipients);
    }
}

//////////////////////////////////////////////////////////////////////////////
