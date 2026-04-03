/// ///////////////////////////////////////////////////////////////////////////
// SecureEmailAddressConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.mapper;

import strata.foundation.core.value.EmailAddress;

public
class SecureEmailAddressConverter
    extends SecureAttributeConverter<EmailAddress>
{
    @Override
    public String
    convertToDatabaseColumn(EmailAddress emailAddress)
    {
        return super.encrypt(emailAddress.toString());
    }

    @Override
    public EmailAddress
    convertToEntityAttribute(String s)
    {
        return EmailAddress.of(super.decrypt(s));
    }
}

//////////////////////////////////////////////////////////////////////////////
