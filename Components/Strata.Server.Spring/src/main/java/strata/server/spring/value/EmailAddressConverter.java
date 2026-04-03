//////////////////////////////////////////////////////////////////////////////
// EmailAddressConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import strata.foundation.core.value.EmailAddress;

@Converter
public
class EmailAddressConverter
    implements AttributeConverter<EmailAddress,String>
{
    @Override
    public String
    convertToDatabaseColumn(EmailAddress attribute)
    {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public EmailAddress
    convertToEntityAttribute(String dbData)
    {

        return dbData != null ? new EmailAddress(dbData) : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
