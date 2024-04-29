//////////////////////////////////////////////////////////////////////////////
// OnCommitTextMessageSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import jakarta.inject.Inject;
import strata.server.core.inject.ITextingConfigurationProvider;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;

public
class OnCommitTextMessageSender
    implements ITextMessageSender
{
    private ITextMessageSender                implementation;
    private IUnitOfWorkSynchronizationManager manager;

    @Inject
    public OnCommitTextMessageSender(
        ITextingConfigurationProvider     cfg,
        IUnitOfWorkSynchronizationManager mgr)
    {
        implementation = new TeleSignMessageSender(cfg);
        manager = mgr;
    }

    @Override
    public ITextMessageSender
    open()
    {
        manager.executeAfterCommit(() -> implementation.open());
        return this;
    }

    @Override
    public ITextMessageSender
    close()
    {
        manager.executeAfterCommit(() -> implementation.close());
        return this;
    }

    @Override
    public ITextMessageSender
    send(ITextMessage message)
    {
        manager.executeAfterCommit(() -> implementation.send(message));
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
