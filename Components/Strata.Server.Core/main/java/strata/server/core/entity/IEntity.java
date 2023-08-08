//////////////////////////////////////////////////////////////////////////////
// IEntity.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;

public
interface IEntity<K extends Serializable,E>
    extends Serializable
{
    E
    setPrimaryId(K primaryId);

    E
    setVersion(Integer version);

    E
    setCreated(Instant created);

    E
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