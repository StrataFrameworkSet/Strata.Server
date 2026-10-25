//////////////////////////////////////////////////////////////////////////////
// StringSetConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.*;
import java.util.stream.Collectors;

@Converter
public
class StringSetConverter
    implements AttributeConverter<Set<String>,String>
{
    @Override
    public String
    convertToDatabaseColumn(Set<String> attribute)
    {
        if (Objects.isNull(attribute))
            return null;

        return
            attribute
                .stream()
                .map(StringSetConverter::escapeCommas)
                .collect(Collectors.joining(","));
    }

    @Override
    public Set<String>
    convertToEntityAttribute(String dbData)
    {
        Set<String> result = new LinkedHashSet<>();

        if (Objects.isNull(dbData) || dbData.isEmpty())
            return result;

        StringBuilder current = new StringBuilder();
        int i = 0;

        while (i < dbData.length())
        {
            char c = dbData.charAt(i);

            if (c == '\\' && i + 1 < dbData.length())
            {
                current.append(dbData.charAt(i + 1));
                i += 2;
            }
            else if (c == ',')
            {
                result.add(current.toString());
                current.setLength(0);
                i++;
            }
            else
            {
                current.append(c);
                i++;
            }
        }

        result.add(current.toString());

        return result;
    }

    private static String
    escapeCommas(String element)
    {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < element.length(); i++)
        {
            char c = element.charAt(i);

            if (c == '\\' || c == ',')
                result.append('\\');

            result.append(c);
        }

        return result.toString();
    }
}

//////////////////////////////////////////////////////////////////////////////
