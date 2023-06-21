//////////////////////////////////////////////////////////////////////////////
// IRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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
    findAllById(Iterable<K> primaryIds);

    Stream<E>
    findAllByIdThenStream(Iterable<K> primaryIds);

    boolean
    existsById(K primaryId);

    Map<K,Boolean>
    existsAllById(Iterable<K> primaryIds);
}

//////////////////////////////////////////////////////////////////////////////