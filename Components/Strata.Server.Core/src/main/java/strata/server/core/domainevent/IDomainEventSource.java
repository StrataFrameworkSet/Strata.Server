//////////////////////////////////////////////////////////////////////////////
// IDomainEventSource.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.domainevent;

import java.util.Set;

public
interface IDomainEventSource<
    S extends IDomainEventSource<S,E,O>,
    E extends IDomainEvent<S>,
    O extends IDomainEventObserver<E>>
{
    S
    attachFrom(S other);

    S
    attach(Set<O> observers);

    S
    attach(O observer);

    S
    detach(O observer);

    Set<O>
    getObservers();

    boolean
    has(O observer);

    S
    notify(E event);
}

//////////////////////////////////////////////////////////////////////////////