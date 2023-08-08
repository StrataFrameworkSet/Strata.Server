//////////////////////////////////////////////////////////////////////////////
// FooBarRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.repository;

import jakarta.inject.Inject;
import strata.server.core.entity.IFooBar;
import strata.server.core.unitofwork.IUnitOfWork;

import java.util.Map;
import java.util.stream.Stream;

public
class FooBarRepository
    extends AbstractRepository<Long,IFooBar>
    implements IFooBarRepository
{
    @Inject
    public
    FooBarRepository(IUnitOfWork uow)
    {
        super(IFooBar.class,"primaryId",uow);
        getUnitOfWork()
            .registerQuery(
                "findByBaz",
                "select foobar from AbstractFooBar foobar where foobar.baz = :baz");
    }

    @Override
    public Stream<IFooBar>
    findByBaz(String baz)
    {
        return
            getUnitOfWork()
                .findManyByQuery(
                    IFooBar.class,
                    "findByBaz",
                    Map.of("baz",baz))
                .stream();
    }
}

//////////////////////////////////////////////////////////////////////////////
