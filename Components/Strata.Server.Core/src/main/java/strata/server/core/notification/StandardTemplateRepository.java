//////////////////////////////////////////////////////////////////////////////
// StandardTemplateRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public
class StandardTemplateRepository
    implements ITemplateRepository
{
    private final Map<String,String> itsTemplates;

    public StandardTemplateRepository()
    {
        itsTemplates = new HashMap<>();
    }

    @Override
    public String
    getTemplate(String templateKey)
    {
        if (!hasTemplate(templateKey))
            throw
                new NoSuchElementException(
                    "Template '" + templateKey + "' not found");

        return itsTemplates.get(templateKey);
    }

    @Override
    public boolean
    hasTemplate(String templateKey)
    {
        return itsTemplates.containsKey(templateKey);
    }

    public StandardTemplateRepository
    insert(String templateKey,String template)
    {
        itsTemplates.put(templateKey,template);
        return this;
    }

    public StandardTemplateRepository
    remove(String templateKey)
    {
        itsTemplates.remove(templateKey);
        return this;
    }
}

//////////////////////////////////////////////////////////////////////////////
