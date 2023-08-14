//////////////////////////////////////////////////////////////////////////////
// IUnitOfWork.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.unitofwork;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public
interface IUnitOfWork
{
    <E,S extends E> S
    save(S entity);

    <E,S extends E> List<S>
    saveAll(Iterable<S> entities);

    <E> void
    delete(E entity);

    <E> void
    deleteAll(Iterable<? extends E> entities);

    <K extends Serializable,E> Optional<E>
    findById(Class<E> type,K id);

    <E> List<E>
    findAll(Class<E> type);

    <K extends Serializable,E> List<E>
    findAllByIdIn(Class<E> type,String idProperty,Iterable<K> ids);

    <E> Optional<E>
    findOneByCriteria(Class<E> type,Map<String,Object> propertyCriteria);

    <E> List<E>
    findManyByCriteria(Class<E> type,String propertyName,Object propertyValue);

    <E> List<E>
    findManyByCriteria(Class<E> type,Map<String,Object> propertyCriteria);

    <E> Optional<E>
    findOneByQuery(Class<E> type,String queryName,Map<String,Object> parameters);

    <E> List<E>
    findManyByQuery(Class<E> type,String queryName,Map<String,Object> parameters);

    <K extends Serializable,E> boolean
    existsById(Class<E> type,String idProperty,K id);

    <K extends Serializable,E> Map<K,Boolean>
    existsByIdIn(Class<E> type,String idProperty,Iterable<K> ids);

    <E,S extends E> boolean
    isManaged(S entity);

    IUnitOfWork
    begin();

    IUnitOfWork
    commit();

    IUnitOfWork
    rollback();

    IUnitOfWork
    registerQuery(String queryName,String queryBody);

    IUnitOfWork
    unregisterQuery(String queryName);

    Map<String,Boolean>
    confirmQueryRegistration(String... queryNames);
}

//////////////////////////////////////////////////////////////////////////////