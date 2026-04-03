//////////////////////////////////////////////////////////////////////////////
// IFoo.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;

import java.io.Serializable;
import java.time.Instant;

public
interface IFoo
    extends Serializable
{
    IFoo
    setPrimaryId(Long primaryId);

    IFoo
    setVersion(Integer version);

    IFoo
    setCreated(Instant created);

    IFoo
    setLastModified(Instant lastModified);

    IFoo
    setName(String nm);

    Long
    getPrimaryId();

    Integer
    getVersion();

    Instant
    getCreated();

    Instant
    getLastModified();

    String
    getName();
}

//////////////////////////////////////////////////////////////////////////////