//////////////////////////////////////////////////////////////////////////////
// OutboxEventSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.outbox;

import jakarta.inject.Inject;
import strata.foundation.core.event.*;

import java.util.concurrent.CompletableFuture;

public
class OutboxEventSender<E>
    extends    AbstractOutboxSender<E>
    implements IEventSender<E>
{
    @Inject
    public
    OutboxEventSender(
        IOutboxEventFactory<E> factory,
        IOutboxEventRepository repository,
        boolean                 autoDelete)
    {
        super(factory, repository, autoDelete);
    }

    public
    OutboxEventSender(
        IOutboxEventFactory<E> factory,
        IOutboxEventRepository repository)
    {
        super(factory,repository);
    }

    @Override
    public IEventSender<E>
    open() throws Exception
    {
        return this;
    }

    @Override
    public IEventSender<E>
    close() throws Exception
    {
        return this;
    }

    @Override
    public ICompletableSendResult<E>
    send(E event)
    {
        try
        {
            super.doSend(event);

            return
                new CompletableSendResult<>(
                    CompletableFuture.completedFuture(
                        new SendResult<>(event)));
        }
        catch (Throwable t)
        {
            return
                new CompletableSendResult<>(
                    CompletableFuture.completedFuture(
                        new SendResult<>(t)));
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
