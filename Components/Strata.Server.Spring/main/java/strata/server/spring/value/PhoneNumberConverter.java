//////////////////////////////////////////////////////////////////////////////
// PhoneNumberConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import jakarta.persistence.AttributeConverter;
import strata.foundation.core.value.PhoneNumber;

public
class PhoneNumberConverter
    implements AttributeConverter<PhoneNumber,String>
{
    @Override
    public String
    convertToDatabaseColumn(PhoneNumber attribute)
    {
        return attribute.getDigitsOnly();
    }

    @Override
    public PhoneNumber
    convertToEntityAttribute(String dbData)
    {
        return new PhoneNumber(dbData);
    }
}

//////////////////////////////////////////////////////////////////////////////
