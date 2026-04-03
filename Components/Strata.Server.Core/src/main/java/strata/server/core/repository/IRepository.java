//////////////////////////////////////////////////////////////////////////////
// IRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public
interface IRepository<K,E>
{
    <S extends E> S
    save(S entity);

    <S extends E> List<S>
    saveAll(Iterable<S> entities);

    void
    delete(E entity);

    void
    deleteAll(Iterable<? extends E> entities);

    Optional<E>
    findById(K primaryId);

    List<E>
    findAll();

    /*
    List<E>
    findAllByIdIn(Iterable<K> primaryIds);
    */
    boolean
    existsById(K primaryId);
    /*
    Map<K,Boolean>
    existsAllByIdIn(Iterable<K> primaryIds);
     */
}

//////////////////////////////////////////////////////////////////////////////