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
        return null;
    }

    @Override
    public ITextMessageSender
    close()
    {
        manager.executeAfterCommit(() -> implementation.close());
        return null;
    }

    @Override
    public ITextMessageSender
    send(ITextMessage message)
    {
        manager.executeAfterCommit(() -> implementation.send(message));
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
