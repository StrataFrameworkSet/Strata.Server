//////////////////////////////////////////////////////////////////////////////
// ActionQueueTextMessageSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import jakarta.inject.Inject;
import strata.foundation.core.action.IActionQueue;
import strata.server.core.inject.ITextingConfigurationProvider;

public
class ActionQueueTextMessageSender
    implements ITextMessageSender
{
    private ITextMessageSender implementation;
    private IActionQueue       queue;

    @Inject
    public ActionQueueTextMessageSender(ITextingConfigurationProvider cfg,IActionQueue q)
    {
        implementation = new TeleSignMessageSender(cfg);
        queue          = q;
    }

    @Override
    public ITextMessageSender
    open()
    {
        queue.insert(() -> implementation.open());
        return null;
    }

    @Override
    public ITextMessageSender
    close()
    {
        queue.insert(() -> implementation.close());
        return null;
    }

    @Override
    public ITextMessageSender
    send(ITextMessage message)
    {
        queue.insert(() -> implementation.send(message));
        return null;
    }

    @Override
    public boolean
    isOpen()
    {
        return implementation.isOpen();
    }
}

//////////////////////////////////////////////////////////////////////////////
