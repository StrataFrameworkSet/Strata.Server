/// ///////////////////////////////////////////////////////////////////////////
// SecurePhoneNumberConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.mapper;

import strata.foundation.core.value.PhoneNumber;

public
class SecurePhoneNumberConverter
    extends SecureAttributeConverter<PhoneNumber>
{
    @Override
    public String
    convertToDatabaseColumn(PhoneNumber phoneNumber)
    {
        return super.encrypt(phoneNumber.toString());
    }

    @Override
    public PhoneNumber
    convertToEntityAttribute(String s)
    {
        return PhoneNumber.of(super.decrypt(s));
    }
}

//////////////////////////////////////////////////////////////////////////////
