//////////////////////////////////////////////////////////////////////////////
// ITransactionalEventProcessor.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.event;

import jakarta.transaction.Transactional;
import strata.foundation.core.event.IEventProcessor;
import strata.foundation.core.event.ProcessingException;

import java.util.Collection;

public
interface ITransactionalEventProcessor<E>
    extends IEventProcessor<E>
{
    @Override
    @Transactional
    void
    process(Collection<E> events) throws ProcessingException;
}

//////////////////////////////////////////////////////////////////////////////