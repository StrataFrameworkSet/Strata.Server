//////////////////////////////////////////////////////////////////////////////
// TeleSignMessageSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import com.google.gson.JsonObject;
import com.telesign.MessagingClient;
import com.telesign.RestClient;
import jakarta.inject.Inject;
import strata.foundation.core.value.PhoneNumber;
import strata.server.core.inject.ITextingConfigurationProvider;

import java.util.Map;

public
class TeleSignMessageSender
    implements ITextMessageSender
{
    private final ITextingConfigurationProvider itsConfiguration;
    private MessagingClient                     itsClient;

    @Inject
    TeleSignMessageSender(ITextingConfigurationProvider configuration)
    {
        itsConfiguration = configuration;
        itsClient = null;
    }

    @Override
    public ITextMessageSender
    open()
    {
        Map<String,String> configuration = itsConfiguration.get();
        String             customerId = configuration.get("telesign.customerId");
        String             apiKey = configuration.get("telesign.apiKey");

        itsClient = new MessagingClient(customerId,apiKey);
        return this;
    }

    @Override
    public ITextMessageSender
    close()
    {
        if (isOpen())
            itsClient = null;

        return this;
    }

    @Override
    public ITextMessageSender
    send(ITextMessage message)
    {
        for (PhoneNumber recipient: message.getRecipients())
        {
            try
            {
                RestClient.TelesignResponse response =
                    getClient().message(
                        recipient.getDigitsOnly(),
                        message.getContent(),
                        "OTP",
                        null);
                int attempts = 1;

                while ((!response.ok) && (attempts++ < 3))
                {
                    JsonObject data = response.json;

                    if (
                        data.has("code") &&
                        data.getAsInt() == 10019)
                    {
                        Thread.currentThread().sleep(1100);
                        response =
                            getClient().message(
                                recipient.getDigitsOnly(),
                                message.getContent(),
                                "OTP",
                                null);
                    }
                    else
                        throw
                            new RuntimeException(
                                "send failed: status=" +
                                    response.statusCode +
                                    " body=" + response.body);
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        return this;
    }

    @Override
    public boolean
    isOpen()
    {
        return itsClient != null;
    }

    private MessagingClient
    getClient()
    {
        if (!isOpen())
            throw new IllegalStateException("client not open");

        return itsClient;
    }
}

//////////////////////////////////////////////////////////////////////////////
