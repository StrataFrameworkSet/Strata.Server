//////////////////////////////////////////////////////////////////////////////
// FooBarA.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;

public
class FooBarA
    extends AbstractFooBar
{
    public
    FooBarA() { super(); }

    @Override
    protected IFooBar
    getSelf()
    {
        return this;
    }
}

//////////////////////////////////////////////////////////////////////////////
