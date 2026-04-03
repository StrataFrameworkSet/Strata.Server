//////////////////////////////////////////////////////////////////////////////
// StringToEmailAddressConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.mapper;

import org.modelmapper.AbstractConverter;
import strata.foundation.core.value.EmailAddress;

public
class StringToEmailAddressConverter
    extends AbstractConverter<String,EmailAddress>
{
    @Override
    protected EmailAddress
    convert(String source)
    {
        return source != null ? new EmailAddress(source) : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
