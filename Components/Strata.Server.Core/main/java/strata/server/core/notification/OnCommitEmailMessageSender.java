//////////////////////////////////////////////////////////////////////////////
// OnCommitEmailMessageSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.server.core.inject.IEmailConfigurationProvider;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;

public
class OnCommitEmailMessageSender
    implements IEmailMessageSender
{
    private IEmailMessageSender               implementation;
    private IUnitOfWorkSynchronizationManager manager;
    private Logger                            logger;

    @Inject
    public
    OnCommitEmailMessageSender(
        IEmailConfigurationProvider       cfg,
        IUnitOfWorkSynchronizationManager mgr)
    {
        implementation = new JavaMailMessageSender(cfg);
        manager        = mgr;
        logger         = LogManager.getLogger(OnCommitEmailMessageSender.class);
    }

    @Override
    public IEmailMessageSender
    open()
    {
        manager.executeAfterCommit(
            () ->
            {
                logger.debug("Executing OnCommitEmailMessageSender.open()");
                implementation.open();
            });
        return this;
    }

    @Override
    public IEmailMessageSender
    close()
    {
        manager.executeAfterCommit(
            () ->
            {
                logger.debug("Executing OnCommitEmailMessageSender.close()");
                implementation.close();
            });
        return this;
    }

    @Override
    public IEmailMessageSender
    send(IEmailMessage message)
    {
        manager.executeAfterCommit(
            () ->
            {
                logger.debug("Executing OnCommitEmailMessageSender.send(message)");
                implementation.send(message);
            });
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
