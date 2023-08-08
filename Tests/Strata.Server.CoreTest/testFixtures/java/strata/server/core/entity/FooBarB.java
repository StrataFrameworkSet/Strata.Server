//////////////////////////////////////////////////////////////////////////////
// FooBarA.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;

public
class FooBarB
    extends AbstractFooBar
{
    private Boolean indicator;

    public FooBarB()
    {
        super();
        indicator = null;
    }

    public FooBarB
    setIndicator(Boolean i)
    {
        indicator = i;
        return this;
    }

    public Boolean
    getIndicator() { return indicator; }

    @Override
    protected IFooBar
    getSelf()
    {
        return this;
    }
}

//////////////////////////////////////////////////////////////////////////////
