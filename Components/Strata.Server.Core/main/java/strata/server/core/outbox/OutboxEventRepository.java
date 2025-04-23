//////////////////////////////////////////////////////////////////////////////
// OutboxEventRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.outbox;

import strata.server.core.repository.AbstractRepository;
import strata.server.core.unitofwork.IUnitOfWork;

import java.util.UUID;

public
class OutboxEventRepository
    extends AbstractRepository<UUID,OutboxEvent>
    implements IOutboxEventRepository
{
    public OutboxEventRepository(IUnitOfWork uow)
    {
        super(OutboxEvent.class,"id",uow);
    }
}

//////////////////////////////////////////////////////////////////////////////
