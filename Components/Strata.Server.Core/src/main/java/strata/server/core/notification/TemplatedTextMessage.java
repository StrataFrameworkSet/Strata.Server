//////////////////////////////////////////////////////////////////////////////
// TemplatedTextMessage.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import org.apache.commons.lang3.StringUtils;
import strata.foundation.core.value.PhoneNumber;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public
class TemplatedTextMessage
    extends AbstractTextMessage
{
    private final ITemplateRepository itsRepository;
    private final String              itsTemplateKey;
    private final Map<String,String>  itsParameters;

    public
    TemplatedTextMessage(
        Set<PhoneNumber> recipients,
        ITemplateRepository repository,
        String templateKey,
        Map<String,String> parameters)
    {
        super(recipients);
        itsRepository = repository;
        itsTemplateKey = templateKey;
        itsParameters = new HashMap<>(parameters);

        if (itsRepository == null)
            throw new NullPointerException("repository must be provided");

        if (itsTemplateKey == null)
            throw new NullPointerException("templateKey must be provided");

        if (!itsRepository.hasTemplate(itsTemplateKey))
            throw
                new IllegalArgumentException(
                    "no template found for '" + itsTemplateKey + "'");
    }

    @Override
    public String
    getContent()
    {
        String template = getTemplate();
        String content  = replaceParameters(template);

        return content;
    }

    private String
    getTemplate()
    {
        return itsRepository.getTemplate(itsTemplateKey);
    }

    private String
    replaceParameters(String template)
    {
        Set<String> parameterKeys = itsParameters.keySet();
        Collection<String> parameterValues = itsParameters.values();

        return
            StringUtils.replaceEach(
                template,
                parameterKeys.toArray(new String[parameterKeys.size()]),
                parameterValues.toArray(new String[parameterValues.size()]));
    }
}

//////////////////////////////////////////////////////////////////////////////
