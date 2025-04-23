//////////////////////////////////////////////////////////////////////////////
// OutboxEmailMessageSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.outbox;

import jakarta.inject.Inject;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.IEmailMessageSender;

public
class OutboxEmailMessageSender
    extends AbstractOutboxSender<IEmailMessage>
    implements IEmailMessageSender
{
    @Inject
    public
    OutboxEmailMessageSender(
        IOutboxEventFactory<IEmailMessage> factory,
        IOutboxEventRepository repository,
        boolean                             autoDelete)
    {
        super(factory,repository,autoDelete);
    }

    public
    OutboxEmailMessageSender(
        IOutboxEventFactory<IEmailMessage> factory,
        IOutboxEventRepository repository)
    {
        super(factory,repository);
    }

    @Override
    public IEmailMessageSender
    open()
    {
        return this;
    }

    @Override
    public IEmailMessageSender
    close()
    {
        return this;
    }

    @Override
    public IEmailMessageSender
    send(IEmailMessage message)
    {
        try
        {
            super.doSend(message);
            return this;
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
