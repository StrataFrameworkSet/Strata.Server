//////////////////////////////////////////////////////////////////////////////
// PropertiesBasedEmailConfigurationProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.inject;

import jakarta.inject.Inject;
import strata.foundation.core.configuration.IConfiguration;

import java.util.HashMap;
import java.util.Map;

public
class SecureEmailConfigurationProvider
    implements IEmailConfigurationProvider
{
    private final IConfiguration itsConfiguration;
    private final String         itsMailHostKey;
    private final String         itsMailPortKey;
    private final String         itsMailUserKey;
    private final String         itsMailPasswordKey;

    @Inject
    public
    SecureEmailConfigurationProvider(IConfiguration configuration)
    {
        this(
            configuration,
            "mail.smtp.host",
            "mail.smtp.port",
            "mail.smtp.user",
            "mail.smtp.password");
    }

    public
    SecureEmailConfigurationProvider(
        IConfiguration configuration,
        String         mailHostKey,
        String         mailPortKey,
        String         mailUserKey,
        String         mailPasswordKey)
    {
        itsConfiguration = configuration;
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
            itsConfiguration.getProperty(itsMailHostKey));
        configuration.put(
            itsMailPortKey,
            itsConfiguration.getProperty(itsMailPortKey));
        configuration.put(
            itsMailUserKey,
            itsConfiguration.getProperty(itsMailUserKey));
        configuration.put(
            itsMailPasswordKey,
            itsConfiguration.getProperty(itsMailPasswordKey));
        configuration.put("mail.smtp.auth","true");
        configuration.put("mail.smtp.starttls.enable","true");

        return configuration;
    }

    private void
    checkKeys(String... keys)
    {
        for (String key:keys)
            if (!itsConfiguration.hasProperty(key))
                throw
                    new IllegalArgumentException(
                        "properties does not contain key: " + key);
    }
}

//////////////////////////////////////////////////////////////////////////////
