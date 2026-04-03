/// ///////////////////////////////////////////////////////////////////////////
// EventProcessorBasedEventListener.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.foundation.core.event.IEventListener;
import strata.foundation.core.event.IEventProcessor;
import strata.foundation.core.event.IEventProcessorSupplier;
import strata.foundation.core.event.StartException;

import java.util.Collection;
import java.util.Optional;

public abstract
class EventProcessorBasedEventListener<
    E,
    P extends IEventProcessor<E>>
    implements IEventListener<E>
{
    private final IEventProcessorSupplier<E,P> supplier;
    private Optional<P>                        processor;
    private final Logger                       logger;

    protected
    EventProcessorBasedEventListener(IEventProcessorSupplier<E,P> supplier)
    {
        this.supplier  = supplier;
        this.processor = Optional.empty();
        this.logger    = LogManager.getLogger(getClass());
    }

    @Override
    public void
    onStart()
        throws StartException
    {
        try
        {
            logger.info("Starting event processing.");
            processor = Optional.of(supplier.get());
        }
        catch (Exception e)
        {
            logger.error("Failed to start event processing.",e);
            throw new StartException("Failed to start event processing.",e);
        }
    }

    @Override
    public void
    onStop()
    {
        logger.info("Stopping event processing.");
        processor = Optional.empty();
    }

    @Override
    public void
    onEvents(Collection<E> events)
    {
        logger.info("Processing {} events", events.size());
        processor.ifPresent(p -> p.process(events));
    }

    @Override
    public void
    onException(Exception e)
    {
        logger.error("Exception during event processing.",e);
    }
}

//////////////////////////////////////////////////////////////////////////////
