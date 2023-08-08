//////////////////////////////////////////////////////////////////////////////
// AbstractEntity.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;

public abstract
class AbstractEntity<K extends Serializable,E>
    implements IEntity<K,E>
{
    private K       itsPrimaryId;
    private Integer itsVersion;
    private Instant itsCreated;
    private Instant itsLastModified;

    protected
    AbstractEntity()
    {
        itsVersion      = null;
        itsCreated      = Instant.now();
        itsLastModified = Instant.now();
    }

    @Override
    public E
    setPrimaryId(K primaryId)
    {
        itsPrimaryId = primaryId;
        return getSelf();
    }

    @Override
    public E
    setVersion(Integer version)
    {
        itsVersion = version;
        return getSelf();
    }

    @Override
    public E
    setCreated(Instant created)
    {
        itsCreated = created;
        return getSelf();
    }

    @Override
    public E
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

    protected abstract E
    getSelf();
}
//////////////////////////////////////////////////////////////////////////////
