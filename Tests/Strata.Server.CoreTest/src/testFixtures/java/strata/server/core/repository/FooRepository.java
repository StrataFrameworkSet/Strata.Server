//////////////////////////////////////////////////////////////////////////////
// FooRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.repository;

import jakarta.inject.Inject;
import strata.server.core.entity.Foo;
import strata.server.core.unitofwork.IUnitOfWork;

import java.util.stream.Stream;

public
class FooRepository
    extends AbstractRepository<Long,Foo>
    implements IFooRepository
{
    @Inject
    public
    FooRepository(IUnitOfWork unitOfWork)
    {
        super(Foo.class,"primaryId",unitOfWork);
    }

    @Override
    public Stream<Foo>
    findByName(String name)
    {
        return
            getUnitOfWork()
                .findManyByCriteria(Foo.class,"name",name)
                .stream();
    }
}

//////////////////////////////////////////////////////////////////////////////
