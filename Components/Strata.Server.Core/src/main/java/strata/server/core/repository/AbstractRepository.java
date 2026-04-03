//////////////////////////////////////////////////////////////////////////////
// AbstractRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.repository;

import strata.server.core.unitofwork.IUnitOfWork;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract
class AbstractRepository<K extends Serializable,E>
    implements IRepository<K,E>
{
    private final Class<E>    entityType;
    private final String      primaryIdProperty;
    private final IUnitOfWork unitOfWork;

    protected
    AbstractRepository(Class<E> type,String idProperty,IUnitOfWork uow)
    {
        entityType        = type;
        primaryIdProperty = idProperty;
        unitOfWork        = uow;
    }

    @Override
    public <S extends E> S
    save(S entity)
    {
        return unitOfWork.save(entity);
    }

    @Override
    public <S extends E> List<S>
    saveAll(Iterable<S> entities)
    {
        return unitOfWork.saveAll(entities);
    }

    @Override
    public void
    delete(E entity)
    {
        unitOfWork.delete(entity);
    }

    @Override
    public void
    deleteAll(Iterable<? extends E> entities)
    {
        unitOfWork.deleteAll(entities);
    }

    @Override
    public Optional<E>
    findById(K primaryId)
    {
        return unitOfWork.findById(entityType,primaryId);
    }

    @Override
    public List<E>
    findAll()
    {
        return unitOfWork.findAll(entityType);
    }

    //@Override
    public List<E>
    findAllByIdIn(Iterable<K> primaryIds)
    {
        return
            unitOfWork.findAllByIdIn(entityType,primaryIdProperty,primaryIds);
    }

    @Override
    public boolean
    existsById(K primaryId)
    {
        return
            unitOfWork.existsById(entityType,primaryIdProperty,primaryId);
    }

    //@Override
    public Map<K,Boolean>
    existsAllByIdIn(Iterable<K> primaryIds)
    {
        return null;
    }

    protected IUnitOfWork
    getUnitOfWork() { return unitOfWork; }

    protected void
    confirmQueryRegistrations(String... queryNames)
    {
        List<String> missingQueries =
            unitOfWork
                .confirmQueryRegistration(queryNames)
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == false)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());

        if (!missingQueries.isEmpty())
            throw
                new IllegalStateException(
                    "The following required queries are not registered: " +
                        missingQueries );

    }
}

//////////////////////////////////////////////////////////////////////////////
