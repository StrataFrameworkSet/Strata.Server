/// ///////////////////////////////////////////////////////////////////////////
// InjectorBasedEventProcessorSupplier.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.event;

import strata.foundation.core.event.IEventProcessor;
import strata.foundation.core.inject.IInjector;
import strata.foundation.core.utility.OptionalExtension;

import java.lang.annotation.Annotation;
import java.util.Optional;

public
class InjectorBasedEventProcessorSupplier<E,P extends IEventProcessor<E>>
    implements java.util.function.Supplier<P>
{
    private final IInjector            injector;
    private final Class<P>             processorType;
    private final Optional<Annotation> annotation;

    public
    InjectorBasedEventProcessorSupplier(
        IInjector  injector,
        Class<P>   processorType)
    {
        this(injector,processorType,null);
    }

    public
    InjectorBasedEventProcessorSupplier(
        IInjector  injector,
        Class<P>   processorType,
        Annotation annotation)
    {
        this.injector = injector;
        this.processorType = processorType;
        this.annotation = Optional.ofNullable(annotation);
    }

    @Override
    public
    P
    get()
    {
        return
            OptionalExtension
                .ifPresentOrElse(
                    annotation,
                    a -> injector.getInstance(processorType,a),
                    () -> injector.getInstance(processorType));
    }
}

//////////////////////////////////////////////////////////////////////////////
