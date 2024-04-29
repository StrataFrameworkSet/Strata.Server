//////////////////////////////////////////////////////////////////////////////
// PersonNameConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import jakarta.persistence.AttributeConverter;
import strata.foundation.core.value.PersonName;

public
class PersonNameConverter
    implements AttributeConverter<PersonName,String>
{
    @Override
    public String
    convertToDatabaseColumn(PersonName attribute)
    {
        return
            new StringBuilder()
                .append(attribute.getLastName())
                .append(',')
                .append(attribute.getFirstName())
                .append(',')
                .append(attribute.getMiddleName().orElse("***"))
                .append(',')
                .append(attribute.getTitle().orElse("***"))
                .toString();
    }

    @Override
    public PersonName
    convertToEntityAttribute(String dbData)
    {
        String[] fields = dbData.split(",");

        if (fields.length != 4)
            throw new IllegalArgumentException(
                "Incorrect number of fields: expected 4, actual " + fields.length);

        if (fields[2].equals("***"))
            if (fields[3].equals("***"))
                return new PersonName(fields[1],fields[0]);
            else
                return new PersonName(fields[3],fields[1],null,fields[0]);
        else
            if (fields[3].equals("***"))
                return new PersonName(fields[1],fields[2],fields[0]);

        return new PersonName(fields[3],fields[1],fields[2],fields[0]);
    }
}

//////////////////////////////////////////////////////////////////////////////
