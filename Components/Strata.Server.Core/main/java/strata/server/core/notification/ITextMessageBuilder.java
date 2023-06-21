//////////////////////////////////////////////////////////////////////////////
// ITextMessageBuilder.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import strata.foundation.core.value.PhoneNumber;

import java.util.Map;
import java.util.Set;

public
interface ITextMessageBuilder
{
    ITextMessageBuilder
    setRecipients(Set<PhoneNumber> recipients);

    ITextMessageBuilder
    addRecipient(PhoneNumber recipient);

    ITextMessageBuilder
    setContent(String content);

    ITextMessageBuilder
    setTemplateKey(String templateKey);

    ITextMessageBuilder
    setParameters(Map<String,String> parameters);

    ITextMessageBuilder
    addParameter(String parameterKey,String parameterValue);

    ITextMessage
    build();

    ITextMessageBuilder
    clear();
}

//////////////////////////////////////////////////////////////////////////////