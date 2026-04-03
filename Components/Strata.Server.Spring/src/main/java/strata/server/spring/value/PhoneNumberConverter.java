//////////////////////////////////////////////////////////////////////////////
// PhoneNumberConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import strata.foundation.core.value.PhoneNumber;

@Converter
public
class PhoneNumberConverter
    implements AttributeConverter<PhoneNumber,String>
{
    @Override
    public String
    convertToDatabaseColumn(PhoneNumber attribute)
    {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public PhoneNumber
    convertToEntityAttribute(String dbData)
    {
        return dbData != null ? new PhoneNumber(dbData) : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
