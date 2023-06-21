//////////////////////////////////////////////////////////////////////////////
// SimpleTextMessage.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import strata.foundation.core.value.PhoneNumber;

import java.util.Set;

public
class SimpleTextMessage
    extends AbstractTextMessage
{
    private final String itsContent;

    public
    SimpleTextMessage(Set<PhoneNumber> recipients,String content)
    {
        super(recipients);
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
