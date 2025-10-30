//////////////////////////////////////////////////////////////////////////////
// PersonNameConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import jakarta.persistence.AttributeConverter;
import strata.foundation.core.value.PersonName;

import java.util.Objects;

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
                .append(normalize(attribute.getLastName())) // 0
                .append(',')
                .append(normalize(attribute.getFirstName())) // 1
                .append(',')
                .append(normalize(attribute.getMiddleName().orElse("***"))) // 2
                .append(',')
                .append(normalize(attribute.getTitle().orElse("***"))) // 3
                .append(',')
                .append(normalize(attribute.getSuffix().orElse("***"))) // 4
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
        return
            field.equals("***")
                ? null
                : field.replace("_", ",");
    }

    private String
    normalize(String value)
    {
        if (Objects.isNull(value))
            return null;

        String trimmed = value.trim();

        return trimmed.replace(",","_");
    }
}

//////////////////////////////////////////////////////////////////////////////
