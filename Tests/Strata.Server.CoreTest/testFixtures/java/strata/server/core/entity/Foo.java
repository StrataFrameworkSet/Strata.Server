//////////////////////////////////////////////////////////////////////////////
// Foo.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;

public
class Foo
    extends AbstractEntity<Long>
    implements IFoo
{
    private String name;

    public
    Foo()
    {
        name = null;
    }

    @Override
    public Foo
    setPrimaryId(Long primaryId)
    {
        return (Foo)super.setPrimaryId(primaryId);
    }

    @Override
    public Long
    getPrimaryId()
    {
        return super.getPrimaryId();
    }

    public Foo
    setName(String n)
    {
        name = n;
        return this;
    }

    public String
    getName() { return name; }
}

//////////////////////////////////////////////////////////////////////////////
