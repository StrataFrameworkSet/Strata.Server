//////////////////////////////////////////////////////////////////////////////
// IFooBarRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.repository;

import strata.server.core.entity.IFooBar;

import java.util.stream.Stream;

public
interface IFooBarRepository
    extends IRepository<Long,IFooBar>
{
    Stream<IFooBar>
    findByBaz(String baz);
}

//////////////////////////////////////////////////////////////////////////////