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

    /*************************************************************************
     * Creates a new instance of {@code AbstractDomainEvent<S>}.
     *
     * @param nm  event name
     * @param src event source
     */
    protected
    AbstractDomainEvent(String nm,S src)
    {
        this(nm,null,src);
    }

    /*************************************************************************
     * Creates a new instance of {@code AbstractDomainEvent<S>}.
     *
     * @param nm  event name
     * @param correlId correlation id
     * @param src event source
     */
    protected
    AbstractDomainEvent(String nm,String correlId,S src)
    {
        name = nm;
        correlationId = correlId;
        timestamp = Instant.now();
        source = src;
    }

    /*************************************************************************
     * {@inheritDoc}
     */
    @Override
    public String
    getName()
    {
        return name;
    }

    /*************************************************************************
     * {@inheritDoc}
     */
    @Override
    public String
    getCorrelationId()
    {
        return correlationId;
    }

    /*************************************************************************
     * {@inheritDoc}
     * @return
     */
    @Override
    public Instant
    getTimestamp()
    {
        return timestamp;
    }

    /*************************************************************************
     * {@inheritDoc}
     */
    @Override
    public S
    getSource()
    {
        return source;
    }
}

//////////////////////////////////////////////////////////////////////////////
