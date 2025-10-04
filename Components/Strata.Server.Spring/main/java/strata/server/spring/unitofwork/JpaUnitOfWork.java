//////////////////////////////////////////////////////////////////////////////
// JpaUnitOfWork.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.unitofwork;

import jakarta.inject.Inject;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.foundation.core.container.Pair;
import strata.foundation.core.utility.Conditional;
import strata.foundation.core.utility.ExtendedOptional;
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
    private EntityManagerFactory            factory;
    private ExtendedOptional<EntityManager> manager;
    private final Map<String,String>        queries;
    private final Logger                    logger;

    @Inject
    public
    JpaUnitOfWork(EntityManagerFactory f)
    {
        factory = f;
        manager = ExtendedOptional.empty();
        queries = new HashMap<>();
        logger = LogManager.getLogger(getClass());
    }

    @Override
    public <E,S extends E> S
    save(S entity)
    {
        logger.trace("save({})",entity);
        return
            manager
                .ifPresentOrThrow(
                    mgr -> mgr.merge(entity),
                    createUnitOfWorkNotBegun());

    }

    @Override
    public <E,S extends E> List<S>
    saveAll(Iterable<S> entities)
    {
        logger.trace("saveAll({})",entities);
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
        logger.trace("delete({})",entity);
        manager
            .ifPresentOrThrowNoReturn(
                mgr -> mgr.remove(mgr.merge(entity)),
                createUnitOfWorkNotBegun());
    }

    @Override
    public <E> void
    deleteAll(Iterable<? extends E> entities)
    {
        logger.trace("deleteAll({})",entities);
        StreamSupport
            .stream(entities.spliterator(),false)
            .forEach(entity -> delete(entity));
    }

    @Override
    public <K extends Serializable,E> Optional<E>
    findById(Class<E> type,K id)
    {
        logger.trace("findById({},{})",type,id);
        return
            manager
                .ifPresentOrThrow(
                    mgr -> Optional.ofNullable(mgr.find(type,id)),
                    createUnitOfWorkNotBegun());
    }

    @Override
    public <E> List<E>
    findAll(Class<E> type)
    {
        logger.trace("findAll({})",type);
        return
            manager
                .ifPresentOrThrow(
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
        logger.trace("findAllByIdIn({},{},{})",type,idProperty,ids);
        return
            manager
                .ifPresentOrThrow(
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
        logger.trace("findOneByCriteria({},{},{})",type,propertyName,propertyValue);
        return
            manager
                .ifPresentOrThrow(
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
        logger.trace("findManyByCriteria({},{},{})",type,propertyName,propertyValue);
        return
            manager
                .ifPresentOrThrow(
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
        logger.trace("findManyByCriteria({},{})",type,propertyCriteria);
        return
            manager
                .ifPresentOrThrow(
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
                                    Conditional
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
        logger.trace("findOneByQuery({},{},{})",type,queryName,parameters);
        try
        {
            return
                manager
                    .ifPresentOrThrow(
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
        logger.trace("findManyByQuery({},{},{})",type,queryName,parameters);
        return
            manager
                .ifPresentOrThrow(
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
        logger.trace("existsById({},{},{})",type,idProperty,id);
        return
            manager
                .ifPresentOrThrow(
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
        logger.trace("existsByIdIn({},{},{})",type,idProperty,ids);
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
    public <E> int
    executeUpdateOrDelete(String queryName,Map<String,Object> parameters)
    {
        logger.trace("executeUpdateOrDelete({},{})",queryName,parameters);
        return
            manager
                .ifPresentOrThrow(
                    mgr ->
                    {
                        Query query =
                            mgr.createQuery(getQuery(queryName));

                        parameters
                            .entrySet()
                            .stream()
                            .forEach(
                                entry ->
                                    query.setParameter(entry.getKey(),entry.getValue()));

                        return query.executeUpdate();
                    },
                    createUnitOfWorkNotBegun());
    }

    @Override
    public <E,S extends E> boolean
    isManaged(S entity)
    {
        return
            manager
                .ifPresentOrThrow(
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
        logger.debug("begin()");
        manager = ExtendedOptional.of(factory.createEntityManager());

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
        logger.debug("commit()");
        return
            manager
                .ifPresentOrThrow(
                    mgr ->
                    {
                        mgr
                            .getTransaction()
                            .commit();
                        return this;
                    },
                    createUnitOfWorkNotBegun());
    }

    @Override
    public IUnitOfWork
    rollback()
    {
        logger.debug("rollback()");
        return
            manager
                .ifPresentOrThrow(
                    mgr ->
                    {
                        mgr
                            .getTransaction()
                            .rollback();
                        return this;
                    },
                    createUnitOfWorkNotBegun());
    }

    @Override
    public void
    close()
    {
        logger.debug("close()");
        manager.ifPresent(
            mgr ->
            {
                if (mgr.getTransaction().isActive())
                {
                    logger.warn("Rolling back uncommitted transaction during close.");
                    mgr.getTransaction().rollback();
                }

                mgr.close();
                manager = ExtendedOptional.empty();
            }
        );
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
