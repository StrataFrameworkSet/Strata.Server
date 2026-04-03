//////////////////////////////////////////////////////////////////////////////
// SecureEmailAddressConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import strata.foundation.core.value.EmailAddress;

public
class SecureEmailAddressConverter
    extends SecureAttributeConverter<EmailAddress>
{
    @Override
    public String
    convertToDatabaseColumn(EmailAddress attribute)
    {
        return
            attribute != null
                ? super.encrypt(attribute.toString())
                : null;
    }

    @Override
    public EmailAddress
    convertToEntityAttribute(String dbData)
    {
        return
            dbData != null
                ? EmailAddress.of(super.decrypt(dbData))
                : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
