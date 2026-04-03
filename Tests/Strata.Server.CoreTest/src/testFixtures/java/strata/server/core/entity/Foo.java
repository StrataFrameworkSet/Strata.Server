//////////////////////////////////////////////////////////////////////////////
// Foo.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;

public
class Foo
{
    private Long primaryId;
    private Integer version;
    private String name;

    public
    Foo()
    {
        primaryId = null;
        version = null;
        name = null;
    }

    public Foo
    setPrimaryId(Long id)
    {
        primaryId = id;
        return this;
    }

    public Foo
    setVersion(Integer ver)
    {
        version = ver;
        return this;
    }

    public Foo
    setName(String n)
    {
        name = n;
        return this;
    }

    public Long
    getPrimaryId() { return primaryId; }

    public Integer
    getVersion() { return version; }

    public String
    getName() { return name; }
}

//////////////////////////////////////////////////////////////////////////////
