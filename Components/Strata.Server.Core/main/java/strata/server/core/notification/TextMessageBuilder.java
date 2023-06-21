//////////////////////////////////////////////////////////////////////////////
// TextMessageBuilder.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import jakarta.inject.Inject;
import strata.foundation.core.value.PhoneNumber;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public
class TextMessageBuilder
    implements ITextMessageBuilder
{
    private final ITemplateRepository itsRepository;
    private Set<PhoneNumber>          itsRecipients;
    private String                    itsContent;
    private String                    itsTemplateKey;
    private Map<String,String>        itsParameters;

    @Inject
    public
    TextMessageBuilder(ITemplateRepository repository)
    {
        itsRepository = repository;
        itsRecipients = new HashSet<>();
        itsContent = null;
        itsTemplateKey = null;
        itsParameters = new HashMap<>();
    }

    @Override
    public ITextMessageBuilder
    setRecipients(Set<PhoneNumber> recipients)
    {
        itsRecipients.clear();
        itsRecipients.addAll(recipients);
        return this;
    }

    @Override
    public ITextMessageBuilder
    addRecipient(PhoneNumber recipient)
    {
        itsRecipients.add(recipient);
        return this;
    }

    @Override
    public ITextMessageBuilder
    setContent(String content)
    {
        if (isTemplated())
        {
            itsTemplateKey = null;
            itsParameters.clear();
        }

        itsContent = content;
        return this;
    }

    @Override
    public ITextMessageBuilder
    setTemplateKey(String templateKey)
    {
        if (isSimple())
            itsContent = null;

        itsTemplateKey = templateKey;
        return this;
    }

    @Override
    public ITextMessageBuilder
    setParameters(Map<String,String> parameters)
    {
        if (isSimple())
            itsContent = null;

        itsParameters.clear();
        itsParameters.putAll(parameters);
        return this;
    }

    @Override
    public ITextMessageBuilder
    addParameter(String parameterKey,String parameterValue)
    {
        if (isSimple())
            itsContent = null;

        itsParameters.put(parameterKey,parameterValue);
        return this;
    }

    @Override
    public ITextMessage
    build()
    {
        if (isSimple())
            return buildSimple();
        else if (isTemplated())
            return buildTemplated();

        throw
            new IllegalStateException(
                "Insufficient information to build text message.");
    }

    @Override
    public ITextMessageBuilder
    clear()
    {
        itsRecipients.clear();
        itsContent = null;
        itsTemplateKey = null;
        itsParameters.clear();
        return this;
    }


    private boolean
    isSimple()
    {
        return itsContent != null;
    }

    private boolean
    isTemplated()
    {
        return itsTemplateKey != null;
    }

    private ITextMessage
    buildSimple()
    {
        try
        {
            return
                new SimpleTextMessage(
                    itsRecipients,
                    itsContent);
        }
        finally
        {
            clear();
        }
    }

    private ITextMessage
    buildTemplated()
    {
        try
        {
            return
                new TemplatedTextMessage(
                    itsRecipients,
                    itsRepository,
                    itsTemplateKey,
                    itsParameters);
        }
        finally
        {
            clear();
        }
    }

}

//////////////////////////////////////////////////////////////////////////////
