//////////////////////////////////////////////////////////////////////////////
// PropertiesBasedTextingConfigurationProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.inject;

import jakarta.inject.Inject;
import strata.foundation.core.configuration.IConfiguration;

import java.util.HashMap;
import java.util.Map;

public
class SecureTextingConfigurationProvider
    implements ITextingConfigurationProvider
{
    private final IConfiguration itsConfiguration;
    private final String         itsCustomerId;
    private final String         itsApiKey;

    @Inject
    public
    SecureTextingConfigurationProvider(IConfiguration configuration)
    {
        this(
            configuration,
            "telesign.customerId",
            "telesign.apiKey");
    }

    public
    SecureTextingConfigurationProvider(
        IConfiguration configuration,
        String         customerId,
        String         apiKey)
    {
        itsConfiguration = configuration;
        itsCustomerId = customerId;
        itsApiKey = apiKey;

        checkKeys(
            itsCustomerId,
            itsApiKey);
    }

    @Override
    public Map<String,String>
    get()
    {
        Map<String,String> configuration = new HashMap<>();

        configuration.put(
            itsCustomerId,
            itsConfiguration.getProperty(itsCustomerId));
        configuration.put(
            itsApiKey,
            itsConfiguration.getProperty(itsApiKey));

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
