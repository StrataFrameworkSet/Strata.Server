//////////////////////////////////////////////////////////////////////////////
// JpaUnitOfWork.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.unitofwork;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import strata.foundation.core.container.Pair;
import strata.foundation.core.utility.BooleanResult;
import strata.foundation.core.utility.OptionalExtension;
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
    private EntityManagerFactory     factory;
    private Optional<EntityManager>  manager;
    private final Map<String,String> queries;

    @Inject
    public
    JpaUnitOfWork(EntityManagerFactory f)
    {
        factory = f;
        manager = Optional.empty();
        queries = new HashMap<>();
    }

    @Override
    public <E,S extends E> S
    save(S entity)
    {
        return
            OptionalExtension
                .ifPresentOrThrow(
                    manager,
                    mgr -> mgr.merge(entity),
                    createUnitOfWorkNotBegun());

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
        OptionalExtension
            .ifPresentOrThrowNoReturn(
                manager,
                mgr -> mgr.remove(entity),
                createUnitOfWorkNotBegun());
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
        return
            OptionalExtension
                .ifPresentOrThrow(
                    manager,
                    mgr -> Optional.ofNullable(mgr.find(type,id)),
                    createUnitOfWorkNotBegun());
    }

    @Override
    public <E> List<E>
    findAll(Class<E> type)
    {
        return
            OptionalExtension
                .ifPresentOrThrow(
                    manager,
                    mgr ->
                    {
                        CriteriaBuilder builder = mgr.getCriteriaBuilder();
                        CriteriaQuery<E> query = builder.createQuery(type);
                        Root<E> root = query.from(type);

                        return
                            mgr
                                .createQuery(query.select(root))
                                .getResultList();
                    },
                    createUnitOfWorkNotBegun());
    }

    @Override
    public <K extends Serializable,E> List<E>
    findAllByIdIn(Class<E> type,String idProperty,Iterable<K> ids)
    {
        return
            OptionalExtension
                .ifPresentOrThrow(
                    manager,
                    mgr ->
                    {
                        CriteriaBuilder builder = mgr.getCriteriaBuilder();
                        CriteriaQuery<E> query = builder.createQuery(type);
                        Root<E> root = query.from(type);

                        return
                            mgr
                                .createQuery(
                                    query
                                        .select(root)
                                        .where(
                                            root
                                                .get(idProperty)
                                                .in(ids)))
                                .getResultList();
                    },
                    createUnitOfWorkNotBegun());
    }

    @Override
    public <E> Optional<E>
    findOneByCriteria(Class<E> type,String propertyName,Object propertyValue)
    {
        return
            OptionalExtension
                .ifPresentOrThrow(
                    manager,
                    mgr ->
                    {
                        CriteriaBuilder builder = mgr.getCriteriaBuilder();
                        CriteriaQuery<E> query = builder.createQuery(type);
                        Root<E> root = query.from(type);

                        try
                        {
                            return
                                Optional.ofNullable(
                                    mgr
                                        .createQuery(
                                            query
                                                .select(root)
                                                .where(
                                                    builder.equal(
                                                        root.get(propertyName),
                                                        propertyValue)))
                                        .getSingleResult());
                        }
                        catch (NoResultException e)
                        {
                            return Optional.empty();
                        }
                    },
                    createUnitOfWorkNotBegun());
    }

    @Override
    public <E> List<E>
    findManyByCriteria(Class<E> type,String propertyName,Object propertyValue)
    {
        return
            OptionalExtension
                .ifPresentOrThrow(
                    manager,
                    mgr ->
                    {
                        CriteriaBuilder builder = mgr.getCriteriaBuilder();
                        CriteriaQuery<E> query = builder.createQuery(type);
                        Root<E> root = query.from(type);

                        return
                            mgr
                                .createQuery(
                                    query
                                        .select(root)
                                        .where(
                                            builder.equal(
                                                root.get(propertyName),
                                                propertyValue)))
                                .getResultList();
                    },
                    createUnitOfWorkNotBegun());
    }

    @Override
    public <E> List<E>
    findManyByCriteria(Class<E> type,Map<String,Object> propertyCriteria)
    {
        return
            OptionalExtension
                .ifPresentOrThrow(
                    manager,
                    mgr ->
                    {
                        CriteriaBuilder builder = mgr.getCriteriaBuilder();
                        CriteriaQuery<E> query = builder.createQuery(type);
                        Root<E> root = query.from(type);
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
                            mgr
                                .createQuery(
                                    query
                                        .select(root)
                                        .where(predicate.get()))
                                .getResultList();
                    },
                    createUnitOfWorkNotBegun());
    }

    @Override
    public <E> Optional<E>
    findOneByQuery(Class<E> type,String queryName,Map<String,Object> parameters)
    {
        try
        {
            return
                OptionalExtension
                    .ifPresentOrThrow(
                        manager,
                        mgr ->
                        {
                            TypedQuery<E> typedQuery =
                                mgr.createQuery(getQuery(queryName),type);

                            parameters
                                .entrySet()
                                .stream()
                                .forEach(
                                    entry ->
                                        typedQuery.setParameter(entry.getKey(),entry.getValue()));

                            return Optional.ofNullable(typedQuery.getSingleResult());
                        },
                        createUnitOfWorkNotBegun());
        }
        catch (NoResultException e)
        {
            return Optional.empty();
        }
    }

    @Override
    public <E> List<E>
    findManyByQuery(Class<E> type,String queryName,Map<String,Object> parameters)
    {
        return
            OptionalExtension
                .ifPresentOrThrow(
                    manager,
                    mgr ->
                    {
                        TypedQuery<E> typedQuery =
                            mgr.createQuery(getQuery(queryName),type);

                        parameters
                            .entrySet()
                            .stream()
                            .forEach(
                                entry ->
                                    typedQuery.setParameter(entry.getKey(),entry.getValue()));

                        return typedQuery.getResultList();
                    },
                    createUnitOfWorkNotBegun());
    }

    @Override
    public <K extends Serializable,E> boolean
    existsById(Class<E> type,String idProperty,K id)
    {
        return
            OptionalExtension
                .ifPresentOrThrow(
                    manager,
                    mgr ->
                    {
                        CriteriaBuilder builder = mgr.getCriteriaBuilder();
                        CriteriaQuery<Long> query = builder.createQuery(Long.class);
                        Root<E> root = query.from(type);

                        return
                            mgr
                                .createQuery(
                                    query
                                        .select(builder.count(root))
                                        .where(builder.equal(root.get(idProperty),id)))
                                .getSingleResult() > 0L;
                    },
                    createUnitOfWorkNotBegun());
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
    public <E,S extends E> boolean
    isManaged(S entity)
    {
        return
            OptionalExtension
                .ifPresentOrThrow(
                    manager,
                    mgr ->
                    {
                        if (mgr.contains(entity))
                            return true;

                        return false;
                    },
                    createUnitOfWorkNotBegun());
    }

    @Override
    public IUnitOfWork
    begin()
    {
        manager = Optional.of(factory.createEntityManager());

        manager
            .ifPresent(
                mgr ->
                    mgr
                        .getTransaction()
                        .begin());
        return this;
    }

    @Override
    public IUnitOfWork
    commit()
    {
            return
                OptionalExtension
                    .ifPresentOrThrow(
                        manager,
                        mgr ->
                        {
                            mgr
                                .getTransaction()
                                .commit();
                            manager = Optional.empty();
                            return this;
                        },
                        createUnitOfWorkNotBegun());
    }

    @Override
    public IUnitOfWork
    rollback()
    {
            return
                OptionalExtension
                    .ifPresentOrThrow(
                        manager,
                        mgr ->
                        {
                            mgr
                                .getTransaction()
                                .rollback();
                            manager = Optional.empty();
                            return this;
                        },
                        createUnitOfWorkNotBegun());
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

    private static RuntimeException
    createUnitOfWorkNotBegun()
    {
        return new RuntimeException("unit of work not begun.");
    }

}

//////////////////////////////////////////////////////////////////////////////
