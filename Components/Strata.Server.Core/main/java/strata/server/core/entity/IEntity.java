//////////////////////////////////////////////////////////////////////////////
// IEntity.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;

import java.time.Instant;

public
interface IEntity<K>
{
    IEntity<K>
    setPrimaryId(K primaryId);

    IEntity<K>
    setVersion(Integer version);

    IEntity<K>
    setCreated(Instant created);

    IEntity<K>
    setLastModified(Instant lastModified);

    K
    getPrimaryId();

    Integer
    getVersion();

    Instant
    getCreated();

    Instant
    getLastModified();
}

//////////////////////////////////////////////////////////////////////////////