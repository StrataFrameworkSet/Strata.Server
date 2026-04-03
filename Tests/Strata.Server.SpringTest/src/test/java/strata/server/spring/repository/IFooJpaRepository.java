//////////////////////////////////////////////////////////////////////////////
// IFooJpaRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import org.springframework.data.repository.ListCrudRepository;
import strata.server.core.entity.Foo;
import strata.server.core.entity.IFoo;

import java.util.Optional;

public
interface IFooJpaRepository
    extends ListCrudRepository<Foo,Long>
{
    Optional<Foo>
    findByName(String name);
}

//////////////////////////////////////////////////////////////////////////////