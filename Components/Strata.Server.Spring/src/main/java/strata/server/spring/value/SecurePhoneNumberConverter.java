/// ///////////////////////////////////////////////////////////////////////////
// SecurePhoneNumberConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import jakarta.persistence.Converter;
import strata.foundation.core.value.PhoneNumber;

@Converter
public
class SecurePhoneNumberConverter
    extends SecureAttributeConverter<PhoneNumber>
{
    @Override
    public String
    convertToDatabaseColumn(PhoneNumber attribute)
    {
        return
            attribute != null
                ? super.encrypt(attribute.toString())
                : null;
    }

    @Override
    public PhoneNumber
    convertToEntityAttribute(String dbData)
    {
        return
            dbData != null
                ? PhoneNumber.of(super.decrypt(dbData))
                : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
