//////////////////////////////////////////////////////////////////////////////
// PhoneNumberToStringConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.mapper;

import org.modelmapper.AbstractConverter;
import strata.foundation.core.value.PhoneNumber;

public
class PhoneNumberToStringConverter
    extends AbstractConverter<PhoneNumber,String>
{
    @Override
    protected String
    convert(PhoneNumber source)
    {
        return source != null ? source.toString() : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
