//////////////////////////////////////////////////////////////////////////////
// AbstractEntityAndDomainEventSource.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;


import jakarta.persistence.MappedSuperclass;
import strata.server.core.domainevent.AbstractDomainEventSource;
import strata.server.core.domainevent.IDomainEvent;
import strata.server.core.domainevent.IDomainEventObserver;
import strata.server.core.domainevent.IDomainEventSource;

import java.io.Serializable;
import java.time.Instant;

public abstract
class AbstractEntityAndDomainEventSource<
    K extends Serializable,
    S extends IDomainEventSource<S,E,O>,
    E extends IDomainEvent<S>,
    O extends IDomainEventObserver<E>>
    extends AbstractDomainEventSource<S,E,O>
    implements IEntity<K,S>,IDomainEventSource<S,E,O>
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
    public S
    setPrimaryId(K primaryId)
    {
        itsPrimaryId = primaryId;
        return getSelf();
    }

    @Override
    public S
    setVersion(Integer version)
    {
        itsVersion = version;
        return getSelf();
    }

    @Override
    public S
    setCreated(Instant created)
    {
        itsCreated = created;
        return getSelf();
    }

    @Override
    public S
    setLastModified(Instant lastModified)
    {
        itsLastModified = lastModified;
        return getSelf();
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
