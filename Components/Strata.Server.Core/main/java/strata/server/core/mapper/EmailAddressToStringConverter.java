//////////////////////////////////////////////////////////////////////////////
// EmailAddressToStringConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.mapper;

import org.modelmapper.AbstractConverter;
import strata.foundation.core.value.EmailAddress;

public
class EmailAddressToStringConverter
    extends AbstractConverter<EmailAddress,String>
{
    @Override
    protected String
    convert(EmailAddress source)
    {
        return source != null ? source.toString() : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
