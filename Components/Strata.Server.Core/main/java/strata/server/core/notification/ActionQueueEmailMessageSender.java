//////////////////////////////////////////////////////////////////////////////
// ActionQueueEmailMessageSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import jakarta.inject.Inject;
import strata.foundation.core.action.IActionQueue;
import strata.server.core.inject.IEmailConfigurationProvider;

public
class ActionQueueEmailMessageSender
    implements IEmailMessageSender
{
    private IEmailMessageSender implementation;
    private IActionQueue        queue;

    @Inject
    public
    ActionQueueEmailMessageSender(IEmailConfigurationProvider cfg,IActionQueue q)
    {
        implementation = new JavaMailMessageSender(cfg);
        queue          = q;
    }

    @Override
    public IEmailMessageSender
    open()
    {
        queue.insert(() -> implementation.open());
        return this;
    }

    @Override
    public IEmailMessageSender
    close()
    {
        queue.insert(() -> implementation.close());
        return this;
    }

    @Override
    public IEmailMessageSender
    send(IEmailMessage message)
    {
        queue.insert(() -> implementation.send(message));
        return this;
    }

    @Override
    public boolean
    isOpen()
    {
        return implementation.isOpen();
    }
}

//////////////////////////////////////////////////////////////////////////////
