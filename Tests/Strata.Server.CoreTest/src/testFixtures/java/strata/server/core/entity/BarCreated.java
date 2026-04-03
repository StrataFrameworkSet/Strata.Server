//////////////////////////////////////////////////////////////////////////////
// BarCreated.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;

import strata.server.core.domainevent.AbstractDomainEvent;

public
class BarCreated
    extends AbstractDomainEvent<Bar>
    implements IBarEvent
{
    public
    BarCreated(Bar src)
    {
        super("BarCreated",src);
    }

    public
    BarCreated(String correlId,Bar src)
    {
        super("BarCreated",correlId,src);
    }
}

//////////////////////////////////////////////////////////////////////////////
