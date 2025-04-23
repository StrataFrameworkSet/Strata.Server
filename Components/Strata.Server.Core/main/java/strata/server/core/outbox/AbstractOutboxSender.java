/// ///////////////////////////////////////////////////////////////////////////
// AbstractOutboxSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.outbox;

public abstract
class AbstractOutboxSender<T>
{
    private final IOutboxEventFactory<T> factory;
    private final IOutboxEventRepository repository;
    private boolean                       autoDelete;


    protected
    AbstractOutboxSender(
        IOutboxEventFactory<T> f,
        IOutboxEventRepository r,
        boolean                 auto)
    {
        factory = f;
        repository = r;
        autoDelete = auto;
    }

    protected
    AbstractOutboxSender(
        IOutboxEventFactory<T> f,
        IOutboxEventRepository r)
    {
        this(f, r, false);
    }

    public boolean
    isOpen()
    {
        return factory != null && repository != null;
    }

    public boolean
    isClosed()
    {
        return !isOpen();
    }

    protected void
    doSend(T source)
        throws Throwable
    {
        OutboxEvent outboxEvent =
            repository.save(factory.create(source));

        if (autoDelete)
            repository.delete(outboxEvent);
    }

}

//////////////////////////////////////////////////////////////////////////////
