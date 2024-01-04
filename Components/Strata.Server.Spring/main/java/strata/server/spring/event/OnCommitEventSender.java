//////////////////////////////////////////////////////////////////////////////
// OnCommitEventSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.event;

import jakarta.inject.Inject;
import strata.foundation.core.concurrent.BlockingBuffer;
import strata.foundation.core.concurrent.IBlockingBuffer;
import strata.foundation.core.event.BlockingBufferBasedCompletableSendResult;
import strata.foundation.core.event.ICompletableSendResult;
import strata.foundation.core.event.IEventSender;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;

public
class OnCommitEventSender<E>
    implements IEventSender<E>
{
    private final IEventSender<E>                   sender;
    private final IUnitOfWorkSynchronizationManager manager;

    @Inject
    public
    OnCommitEventSender(
        IEventSender<E>                   s,
        IUnitOfWorkSynchronizationManager m)
    {
        sender  = s;
        manager = m;
    }

    @Override
    public IEventSender<E>
    open() throws Exception
    {
        manager.executeAfterCommit(() -> sender.open());
        return this;
    }

    @Override
    public IEventSender<E>
    close() throws Exception
    {
        manager.executeAfterCommit(() -> sender.close());
        return this;
    }

    @Override
    public ICompletableSendResult<E>
    send(E event)
    {
        IBlockingBuffer<ICompletableSendResult<E>> buffer = new BlockingBuffer<>();

        manager.executeAfterCommit(() -> buffer.accept(sender.send(event)));
        return new BlockingBufferBasedCompletableSendResult<>(buffer);
    }

    @Override
    public boolean
    isOpen()
    {
        return sender.isOpen();
    }

    @Override
    public boolean
    isClosed()
    {
        return sender.isClosed();
    }
}

//////////////////////////////////////////////////////////////////////////////
