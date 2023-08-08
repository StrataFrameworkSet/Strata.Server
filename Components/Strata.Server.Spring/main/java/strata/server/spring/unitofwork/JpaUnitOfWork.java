//////////////////////////////////////////////////////////////////////////////
// JpaUnitOfWork.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.unitofwork;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.orm.jpa.JpaTransactionManager;
import strata.foundation.core.container.Pair;
import strata.foundation.core.utility.BooleanResult;
import strata.server.core.unitofwork.IUnitOfWork;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public
class JpaUnitOfWork
    implements IUnitOfWork
{
    @PersistenceContext
    private EntityManager              entityManager;
    private final Map<String,String>         queries;

    public
    JpaUnitOfWork(JpaTransactionManager tm)
    {

        //entityManager = em;
        queries = new HashMap<>();
    }

    @Override
    public <E,S extends E> S
    save(S entity)
    {
        if (entityManager.contains(entity))
            return entityManager.merge(entity);

        entityManager.persist(entity);
        return entity;
    }

    @Override
    public <E,S extends E> List<S>
    saveAll(Iterable<S> entities)
    {
        return
            StreamSupport
                .stream(entities.spliterator(),false)
                .map(entity -> save(entity))
                .collect(Collectors.toList());
    }

    @Override
    public <E> void
    delete(E entity)
    {
        entityManager.remove(entity);
    }

    @Override
    public <E> void
    deleteAll(Iterable<? extends E> entities)
    {
        StreamSupport
            .stream(entities.spliterator(),false)
            .forEach(entity -> delete(entity));
    }

    @Override
    public <K extends Serializable,E> Optional<E>
    findById(Class<E> type,K id)
    {
        return Optional.ofNullable(entityManager.find(type,id));
    }

    @Override
    public <E> List<E>
    findAll(Class<E> type)
    {
        CriteriaBuilder  builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);
        Root<E>          root = query.from(type);

        return
            entityManager
                .createQuery(query.select(root))
                .getResultList();
    }

    @Override
    public <K extends Serializable,E> List<E>
    findAllByIdIn(Class<E> type,String idProperty,Iterable<K> ids)
    {
        CriteriaBuilder  builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);
        Root<E>          root = query.from(type);

        return
            entityManager
                .createQuery(
                    query
                        .select(root)
                        .where(
                            root
                                .get(idProperty)
                                .in(ids)))
                .getResultList();
    }

    @Override
    public <E> Optional<E>
    findOneByCriteria(Class<E> type,Map<String,Object> propertyCriteria)
    {
        return Optional.empty();
    }

    @Override
    public <E> List<E>
    findManyByCriteria(Class<E> type,String propertyName,Object propertyValue)
    {
        CriteriaBuilder  builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);
        Root<E>          root = query.from(type);

        return
            entityManager
                .createQuery(
                    query
                        .select(root)
                        .where(
                            builder.equal(
                                root.get(propertyName),
                                propertyValue)))
                .getResultList();
    }

    @Override
    public <E> List<E>
    findManyByCriteria(Class<E> type,Map<String,Object> propertyCriteria)
    {
        CriteriaBuilder  builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(type);
        Root<E>          root = query.from(type);
        AtomicReference<Predicate> predicate = new AtomicReference<>();

        propertyCriteria
            .entrySet()
            .stream()
            .forEach(
                entry ->
                    BooleanResult
                        .of(predicate.get() == null)
                        .ifTrueOrElse(
                            () ->
                                predicate.set(
                                    builder.equal(
                                        root.get(entry.getKey()),
                                        entry.getValue())),
                            () ->
                                predicate.set(
                                    builder
                                        .and(
                                            builder.equal(root.get(entry.getKey()),
                                                entry.getValue())))));
        return
            entityManager
                .createQuery(
                    query
                        .select(root)
                        .where(predicate.get()))
                .getResultList();
    }

    @Override
    public <E> Optional<E>
    findOneByQuery(Class<E> type,String queryName,Map<String,Object> parameters)
    {
        TypedQuery<E> typedQuery =
            entityManager.createQuery(getQuery(queryName),type);

        parameters
            .entrySet()
            .stream()
            .forEach(
                entry ->
                    typedQuery.setParameter(entry.getKey(),entry.getValue()));

        return Optional.ofNullable(typedQuery.getSingleResult());
    }

    @Override
    public <E> List<E>
    findManyByQuery(Class<E> type,String queryName,Map<String,Object> parameters)
    {
        TypedQuery<E> typedQuery =
            entityManager.createQuery(getQuery(queryName),type);

        parameters
            .entrySet()
            .stream()
            .forEach(
                entry ->
                    typedQuery.setParameter(entry.getKey(),entry.getValue()));

        return typedQuery.getResultList();
    }

    @Override
    public <K extends Serializable,E> boolean
    existsById(Class<E> type,String idProperty,K id)
    {
        CriteriaBuilder     builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query   = builder.createQuery(Long.class);
        Root<E>             root    = query.from(type);

        return
            entityManager
                .createQuery(
                    query
                        .select(builder.count(root))
                        .where(builder.equal(root.get(idProperty),id)))
                .getSingleResult() > 0L;
     }

    @Override
    public <K extends Serializable,E> Map<K,Boolean>
    existsByIdIn(Class<E> type,String idProperty,Iterable<K> ids)
    {
        return
            StreamSupport
                .stream(ids.spliterator(),false)
                .map(id -> Pair.create(id,existsById(type,idProperty,id)))
                .collect(
                    Collectors.toMap(
                        pair -> pair.getFirst(),
                        pair -> pair.getSecond()));
    }

    @Override
    public IUnitOfWork
    begin()
    {
        entityManager
            .getTransaction()
            .begin();
        return this;
    }

    @Override
    public IUnitOfWork
    commit()
    {
        entityManager
            .getTransaction()
            .commit();
        return this;
    }

    @Override
    public IUnitOfWork
    rollback()
    {
        entityManager
            .getTransaction()
            .rollback();
        return this;
    }

    @Override
    public
    JpaUnitOfWork
    registerQuery(String queryName,String query)
    {
        queries.put(queryName,query);
        return this;
    }

    @Override
    public JpaUnitOfWork
    unregisterQuery(String queryName)
    {
        queries.remove(queryName);
        return this;
    }

    @Override
    public Map<String,Boolean>
    confirmQueryRegistration(String... queryNames)
    {
        return
            Arrays
                .stream(queryNames)
                .map(queryName -> Pair.create(queryName,queries.containsKey(queryName)))
                .collect(
                    Collectors.toMap(
                        pair -> pair.getFirst(),
                        pair -> pair.getSecond()));
    }

    protected String
    getQuery(String queryName)
    {
        if (!queries.containsKey(queryName))
            throw new NoSuchElementException(queryName);

        return queries.get(queryName);
    }
}

//////////////////////////////////////////////////////////////////////////////
