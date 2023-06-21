//////////////////////////////////////////////////////////////////////////////
// AbstractEntityAndDomainEventSource.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;


import strata.server.core.domainevent.AbstractDomainEventSource;
import strata.server.core.domainevent.IDomainEvent;
import strata.server.core.domainevent.IDomainEventObserver;
import strata.server.core.domainevent.IDomainEventSource;

import java.time.Instant;

public abstract
class AbstractEntityAndDomainEventSource<
    K,
    S extends IDomainEventSource<S,E,O>,
    E extends IDomainEvent<S>,
    O extends IDomainEventObserver<E>>
    extends AbstractDomainEventSource<S,E,O>
    implements IEntity<K>,IDomainEventSource<S,E,O>
{
    private K       itsPrimaryId;
    private Integer itsVersion;
    private Instant itsCreated;
    private Instant itsLastModified;

    protected
    AbstractEntityAndDomainEventSource()
    {
        itsVersion      = null;
        itsCreated      = Instant.now();
        itsLastModified = Instant.now();
    }

    protected
    AbstractEntityAndDomainEventSource(
        AbstractEntityAndDomainEventSource<K,S,E,O> other)
    {
        super(other);

        itsVersion      = null;
        itsCreated      = Instant.now();
        itsLastModified = Instant.now();
    }

    @Override
    public IEntity<K>
    setPrimaryId(K primaryId)
    {
        itsPrimaryId = primaryId;
        return this;
    }

    @Override
    public IEntity<K>
    setVersion(Integer version)
    {
        itsVersion = version;
        return this;
    }

    @Override
    public IEntity<K>
    setCreated(Instant created)
    {
        itsCreated = created;
        return this;
    }

    @Override
    public IEntity<K>
    setLastModified(Instant lastModified)
    {
        itsLastModified = lastModified;
        return this;
    }

    @Override
    public K
    getPrimaryId()
    {
        return itsPrimaryId;
    }

    @Override
    public Integer
    getVersion()
    {
        return itsVersion;
    }

    @Override
    public Instant
    getCreated()
    {
        return itsCreated;
    }

    @Override
    public Instant
    getLastModified()
    {
        return itsLastModified;
    }
}

//////////////////////////////////////////////////////////////////////////////
