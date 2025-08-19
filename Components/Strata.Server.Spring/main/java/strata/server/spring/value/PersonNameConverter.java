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
        if (attribute == null)
            return null;

        return
            new StringBuilder()
                .append(attribute.getLastName()) // 0
                .append(',')
                .append(attribute.getFirstName()) // 1
                .append(',')
                .append(attribute.getMiddleName().orElse("***")) // 2
                .append(',')
                .append(attribute.getTitle().orElse("***")) // 3
                .append(',')
                .append(attribute.getSuffix().orElse("***")) // 4
                .toString();
    }

    @Override
    public PersonName
    convertToEntityAttribute(String dbData)
    {
        if (dbData == null)
            return null;

        String[] fields = dbData.split(",");

        if (fields.length == 4)
            return new PersonName(
                getFieldValue(fields[3]),  // title
                getFieldValue(fields[1]),  // first name
                getFieldValue(fields[2]),  // middle name
                getFieldValue(fields[0]),  // last name
                null);                     // suffix

        if (fields.length != 5)
            throw new IllegalArgumentException(
                "Incorrect number of fields: expected 5, actual " + fields.length);

        return
            new PersonName(
                getFieldValue(fields[3]),  // title
                getFieldValue(fields[1]),  // first name
                getFieldValue(fields[2]),  // middle name
                getFieldValue(fields[0]),  // last name
                getFieldValue(fields[4])); // suffix
    }

    private String
    getFieldValue(String field)
    {
        return field.equals("***") ? null : field;
    }
}

//////////////////////////////////////////////////////////////////////////////
