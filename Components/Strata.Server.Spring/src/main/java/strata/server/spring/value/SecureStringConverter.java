//////////////////////////////////////////////////////////////////////////////
// SecureEmailAddressConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

public
class SecureStringConverter
    extends SecureAttributeConverter<String>
{
    @Override
    public String
    convertToDatabaseColumn(String attribute)
    {
        return attribute != null ? super.encrypt(attribute) : null;
    }

    @Override
    public String
    convertToEntityAttribute(String dbData)
    {
        return dbData != null ? super.decrypt(dbData) : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
