//////////////////////////////////////////////////////////////////////////////
// OnCommitEmailMessageSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import jakarta.inject.Inject;
import strata.server.core.inject.IEmailConfigurationProvider;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;

public
class OnCommitEmailMessageSender
    implements IEmailMessageSender
{
    private IEmailMessageSender               implementation;
    private IUnitOfWorkSynchronizationManager manager;

    @Inject
    public
    OnCommitEmailMessageSender(
        IEmailConfigurationProvider       cfg,
        IUnitOfWorkSynchronizationManager mgr)
    {
        implementation = new JavaMailMessageSender(cfg);
        manager        = mgr;
    }

    @Override
    public IEmailMessageSender
    open()
    {
        manager.executeAfterCommit(() -> implementation.open());
        return this;
    }

    @Override
    public IEmailMessageSender
    close()
    {
        manager.executeAfterCommit(() -> implementation.close());
        return this;
    }

    @Override
    public IEmailMessageSender
    send(IEmailMessage message)
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
