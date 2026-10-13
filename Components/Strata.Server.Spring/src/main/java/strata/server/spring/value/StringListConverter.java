//////////////////////////////////////////////////////////////////////////////
// StringListConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Converter
public
class StringListConverter
    implements AttributeConverter<List<String>,String>
{
    @Override
    public String
    convertToDatabaseColumn(List<String> attribute)
    {
        if (Objects.isNull(attribute))
            return null;

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < attribute.size(); i++)
        {
            if (i > 0)
                result.append(',');

            String element = attribute.get(i);

            if (Objects.nonNull(element))
                appendEscaped(result,element);
        }

        return result.toString();
    }

    @Override
    public List<String>
    convertToEntityAttribute(String dbData)
    {
        List<String> result = new ArrayList<>();

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

    private static void
    appendEscaped(StringBuilder result,String element)
    {
        for (int i = 0; i < element.length(); i++)
        {
            char c = element.charAt(i);

            if (c == '\\' || c == ',')
                result.append('\\');

            result.append(c);
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
