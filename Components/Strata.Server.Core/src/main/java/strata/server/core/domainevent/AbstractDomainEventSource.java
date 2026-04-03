//////////////////////////////////////////////////////////////////////////////
// AbstractDomainEventSource.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.domainevent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract
class AbstractDomainEventSource<
    S extends IDomainEventSource<S,E,O>,
    E extends IDomainEvent<S>,
    O extends IDomainEventObserver<E>>
    implements IDomainEventSource<S,E,O>
{
    private final Set<O> observers;

    protected
    AbstractDomainEventSource()
    {
        observers = new HashSet<O>();
    }

    protected
    AbstractDomainEventSource(AbstractDomainEventSource<S,E,O> other)
    {
        observers = new HashSet<O>(other.getObservers());
    }

    @Override
    public S
    attachFrom(S other)
    {
        observers.addAll(other.getObservers());
        return getSelf();
    }

    @Override
    public S
    attach(Set<O> observers)
    {
        observers.addAll(observers);
        return getSelf();
    }

    @Override
    public S
    attach(O observer)
    {
        observers.add(observer);
        return getSelf();
    }

    @Override
    public S
    detach(O observer)
    {
        observers.remove(observer);
        return getSelf();
    }

    @Override
    public Set<O>
    getObservers()
    {
        return Collections.unmodifiableSet(observers);
    }

    @Override
    public boolean
    has(O observer)
    {
        return observers.contains(observer);
    }

    @Override
    public S
    notify(E event)
    {
        observers
            .stream()
            .forEach(observer -> observer.onEvent(event));

        return getSelf();
    }

    @SuppressWarnings("unchecked")
    protected S
    getSelf() { return (S)this; }
}

//////////////////////////////////////////////////////////////////////////////
