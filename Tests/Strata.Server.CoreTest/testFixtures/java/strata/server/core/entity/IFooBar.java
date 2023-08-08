//////////////////////////////////////////////////////////////////////////////
// IFooBar.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.entity;

public
interface IFooBar
    extends IEntity<Long,IFooBar>
{
    @Override
    Long
    getPrimaryId();

    IFooBar
    setBaz(String baz);

    String
    getBaz();
}

//////////////////////////////////////////////////////////////////////////////