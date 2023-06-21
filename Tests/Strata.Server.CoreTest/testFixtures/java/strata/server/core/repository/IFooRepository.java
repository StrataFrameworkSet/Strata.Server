//////////////////////////////////////////////////////////////////////////////
// IFooRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.repository;

import strata.server.core.entity.Foo;

import java.util.Optional;

public
interface IFooRepository
    extends IRepository<Long,Foo>
{
    Optional<Foo>
    findByName(String name);

    boolean
    existsByName(String name);
}

//////////////////////////////////////////////////////////////////////////////