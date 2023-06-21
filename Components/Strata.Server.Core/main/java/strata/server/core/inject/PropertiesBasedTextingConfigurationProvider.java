//////////////////////////////////////////////////////////////////////////////
// PropertiesBasedTextingConfigurationProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.inject;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public
class PropertiesBasedTextingConfigurationProvider
    implements IConfigurationProvider
{
    private final Properties itsProperties;
    private final String     itsCustomerId;
    private final String     itsApiKey;

    public
    PropertiesBasedTextingConfigurationProvider(Properties properties)
    {
        this(
            properties,
            "telesign.customerId",
            "telesign.apiKey");
    }

    public
    PropertiesBasedTextingConfigurationProvider(
        Properties properties,
        String     customerId,
        String     apiKey)
    {
        itsProperties = properties;
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
            itsProperties.getProperty(itsCustomerId));
        configuration.put(
            itsApiKey,
            itsProperties.getProperty(itsApiKey));

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
