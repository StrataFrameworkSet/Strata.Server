//////////////////////////////////////////////////////////////////////////////
// AbstractFooBar.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;

public abstract
class AbstractFooBar
    extends AbstractEntity<Long,IFooBar>
    implements IFooBar
{
    private String  baz;

    protected
    AbstractFooBar()
    {
        baz = null;
    }

    @Override
    public IFooBar
    setBaz(String b)
    {
        baz = b;
        return this;
    }

    @Override
    public String
    getBaz()
    {
        return baz;
    }
}

//////////////////////////////////////////////////////////////////////////////
