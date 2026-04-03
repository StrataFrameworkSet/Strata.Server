//////////////////////////////////////////////////////////////////////////////
// PropertiesBasedEmailConfigurationProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.inject;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public
class PropertiesBasedEmailConfigurationProvider
    implements IConfigurationProvider
{
    private final Properties itsProperties;
    private final String     itsMailHostKey;
    private final String     itsMailPortKey;
    private final String     itsMailUserKey;
    private final String     itsMailPasswordKey;

    public
    PropertiesBasedEmailConfigurationProvider(Properties properties)
    {
        this(
            properties,
            "mail.smtp.host",
            "mail.smtp.port",
            "mail.smtp.user",
            "mail.smtp.password");
    }

    public
    PropertiesBasedEmailConfigurationProvider(
        Properties properties,
        String     mailHostKey,
        String     mailPortKey,
        String     mailUserKey,
        String     mailPasswordKey)
    {
        itsProperties = properties;
        itsMailHostKey = mailHostKey;
        itsMailPortKey = mailPortKey;
        itsMailUserKey = mailUserKey;
        itsMailPasswordKey = mailPasswordKey;

        checkKeys(
            itsMailHostKey,
            itsMailPortKey,
            itsMailUserKey,
            itsMailPasswordKey);
    }

    @Override
    public Map<String,String>
    get()
    {
        Map<String,String> configuration = new HashMap<>();

        configuration.put(
            itsMailHostKey,
            itsProperties.getProperty(itsMailHostKey));
        configuration.put(
            itsMailPortKey,
            itsProperties.getProperty(itsMailPortKey));
        configuration.put(
            itsMailUserKey,
            itsProperties.getProperty(itsMailUserKey));
        configuration.put(
            itsMailPasswordKey,
            itsProperties.getProperty(itsMailPasswordKey));
        configuration.put("mail.smtp.auth","true");
        configuration.put("mail.smtp.starttls.enable","true");

        return configuration;
    }

    private void
    checkKeys(String... keys)
    {
        for (String key:keys)
            if (!itsProperties.containsKey(key))
                throw
                    new IllegalArgumentException(
                        "properties does not contain key: " + key);
    }
}

//////////////////////////////////////////////////////////////////////////////
