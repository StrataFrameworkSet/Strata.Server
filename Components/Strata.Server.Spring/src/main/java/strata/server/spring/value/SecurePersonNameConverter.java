/// ///////////////////////////////////////////////////////////////////////////
// SecurePersonNameConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import strata.foundation.core.value.PersonName;

public
class SecurePersonNameConverter
    extends SecureAttributeConverter<PersonName>
{
    private final PersonNameConverter converter;

    public
    SecurePersonNameConverter()
    {
        converter = new PersonNameConverter();
    }

    @Override
    public String
    convertToDatabaseColumn(PersonName attribute)
    {
        String value = converter.convertToDatabaseColumn(attribute);

        return
            value != null
                ? super.encrypt(value)
                : null;
    }

    @Override
    public PersonName
    convertToEntityAttribute(String dbData)
    {
        return
            dbData != null
            ? converter.convertToEntityAttribute(super.decrypt(dbData))
            : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
