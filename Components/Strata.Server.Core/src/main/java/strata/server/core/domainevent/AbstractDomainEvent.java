//////////////////////////////////////////////////////////////////////////////
// AbstractDomainEvent.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.domainevent;

import java.time.Instant;

public abstract
class AbstractDomainEvent<S>
    implements IDomainEvent<S>
{
    private final String  name;
    private final String  correlationId;
    private final Instant timestamp;
    private final S       source;

    protected
    AbstractDomainEvent(String nm,S src)
    {
        this(nm,null,src);
    }

    protected
    AbstractDomainEvent(String nm,String correlId,S src)
    {
        name = nm;
        correlationId = correlId;
        timestamp = Instant.now();
        source = src;
    }

    @Override
    public String
    getName()
    {
        return name;
    }

    @Override
    public String
    getCorrelationId()
    {
        return correlationId;
    }

    @Override
    public Instant
    getTimestamp()
    {
        return timestamp;
    }

    @Override
    public S
    getSource()
    {
        return source;
    }
}

//////////////////////////////////////////////////////////////////////////////
