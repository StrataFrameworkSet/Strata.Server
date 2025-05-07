//////////////////////////////////////////////////////////////////////////////
// SerializableTextMessage.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import strata.foundation.core.value.PhoneNumber;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public
class SerializableTextMessage
    implements Serializable,ITextMessage
{
    private Set<PhoneNumber> recipients;
    private String content;

    public SerializableTextMessage()
    {
        recipients = new HashSet<>();
        content = null;
    }

    public SerializableTextMessage
    setRecipients(Set<PhoneNumber> recipients)
    {
        this.recipients = new HashSet<>(recipients);
        return this;
    }

    public SerializableTextMessage
    setContent(String content)
    {
        this.content = content;
        return this;
    }

    @Override
    public Set<PhoneNumber>
    getRecipients()
    {
        return recipients;
    }

    @Override
    public String
    getContent()
    {
        return content;
    }

    public static SerializableTextMessage
    of(ITextMessage textMessage)
    {
        return
            new SerializableTextMessage()
                .setRecipients(textMessage.getRecipients())
                .setContent(textMessage.getContent());
    }
}

//////////////////////////////////////////////////////////////////////////////
