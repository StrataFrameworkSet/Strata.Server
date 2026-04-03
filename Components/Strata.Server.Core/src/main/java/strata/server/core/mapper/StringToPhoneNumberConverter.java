//////////////////////////////////////////////////////////////////////////////
// StringToPhoneNumberConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.mapper;

import org.modelmapper.AbstractConverter;
import strata.foundation.core.value.PhoneNumber;

public
class StringToPhoneNumberConverter
    extends AbstractConverter<String,PhoneNumber>
{
    @Override
    protected PhoneNumber
    convert(String source)
    {
        return source != null ? new PhoneNumber(source) : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
