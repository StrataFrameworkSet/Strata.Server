//////////////////////////////////////////////////////////////////////////////
// ISpringRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import strata.server.core.repository.IRepository;

@Repository
@Transactional
public
interface ISpringRepository<K,E>
    extends IRepository<K,E>, ListCrudRepository<E,K> {}

//////////////////////////////////////////////////////////////////////////////