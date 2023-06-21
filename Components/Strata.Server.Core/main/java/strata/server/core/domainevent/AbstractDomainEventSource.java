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

    /*************************************************************************
     * Creates  a new instance of {@code AbstractDomainEventSource<S,E,O>}.
     */
    protected
    AbstractDomainEventSource()
    {
        observers = new HashSet<O>();
    }

    /*************************************************************************
     * Creates  a new instance of {@code AbstractDomainEventSource<S,E,O>}.
     */
    protected
    AbstractDomainEventSource(AbstractDomainEventSource<S,E,O> other)
    {
        observers = new HashSet<O>(other.getObservers());
    }

    /*************************************************************************
     * {@inheritDoc}
     */
    @Override
    public S
    attachFrom(S other)
    {
        observers.addAll(other.getObservers());
        return getSelf();
    }

    /*************************************************************************
     * {@inheritDoc}
     */
    @Override
    public S
    attach(Set<O> observers)
    {
        observers.addAll(observers);
        return getSelf();
    }

    /*************************************************************************
     * {@inheritDoc}
     */
    @Override
    public S
    attach(O observer)
    {
        observers.add(observer);
        return getSelf();
    }

    /*************************************************************************
     * {@inheritDoc}
     */
    @Override
    public S
    detach(O observer)
    {
        observers.remove(observer);
        return getSelf();
    }

    /*************************************************************************
     * {@inheritDoc}
     */
    @Override
    public Set<O>
    getObservers()
    {
        return Collections.unmodifiableSet(observers);
    }

    /*************************************************************************
     * {@inheritDoc}
     */
    @Override
    public boolean
    has(O observer)
    {
        return observers.contains(observer);
    }

    /*************************************************************************
     * {@inheritDoc}
     */
    @Override
    public S
    notify(E event)
    {
        observers
            .stream()
            .forEach(observer -> observer.onEvent(event));

        return getSelf();
    }

    /*************************************************************************
     * Returns a reference to this.
     *
     * @return reference to this for method chaining
     */
    protected abstract S
    getSelf();
}

//////////////////////////////////////////////////////////////////////////////
