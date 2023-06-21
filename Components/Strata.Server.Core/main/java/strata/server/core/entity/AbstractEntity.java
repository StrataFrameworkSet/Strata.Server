//////////////////////////////////////////////////////////////////////////////
// AbstractEntity.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;

import java.time.Instant;

public abstract
class AbstractEntity<K>
    implements IEntity<K>
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
