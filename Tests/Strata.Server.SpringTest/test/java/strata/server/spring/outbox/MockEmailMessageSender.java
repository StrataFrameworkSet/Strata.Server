/// ///////////////////////////////////////////////////////////////////////////
// MockEmailMessageSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.outbox;

import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.IEmailMessageSender;

public
class MockEmailMessageSender
    implements IEmailMessageSender
{
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
        System.out.println(message);
        return this;
    }

    @Override
    public boolean
    isOpen()
    {
        return true;
    }
}

//////////////////////////////////////////////////////////////////////////////
