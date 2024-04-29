//////////////////////////////////////////////////////////////////////////////
// OnCommitEventSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.event;

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
    private final IEventSender<E> implementation;
    private final IUnitOfWorkSynchronizationManager manager;

    @Inject
    public
    OnCommitEventSender(IEventSender<E> imp,IUnitOfWorkSynchronizationManager mgr)
    {
        implementation = imp;
        manager        = mgr;
    }

    @Override
    public IEventSender<E>
    open() throws Exception
    {
        manager.executeAfterCommit(() -> implementation.open());
        return this;
    }

    @Override
    public IEventSender<E>
    close() throws Exception
    {
        manager.executeAfterCommit(() -> implementation.close());
        return this;
    }

    @Override
    public ICompletableSendResult<E>
    send(E event)
    {
        IBlockingBuffer<ICompletableSendResult<E>> buffer = new BlockingBuffer<>();

        manager.executeAfterCommit(() -> buffer.accept(implementation.send(event)));
        return new BlockingBufferBasedCompletableSendResult<>(buffer);
    }

    @Override
    public boolean
    isOpen()
    {
        return implementation.isOpen();
    }

    @Override
    public boolean
    isClosed()
    {
        return implementation.isClosed();
    }
}

//////////////////////////////////////////////////////////////////////////////
