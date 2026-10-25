//////////////////////////////////////////////////////////////////////////////
// StringMapConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import strata.foundation.core.mapper.ObjectMapperSupplier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Converter
public
class StringMapConverter
    implements AttributeConverter<Map<String,Object>,String>
{
    private final ObjectMapper mapper;

    public
    StringMapConverter()
    {
        this.mapper = new ObjectMapperSupplier().get();
    }

    @Override
    public String
    convertToDatabaseColumn(Map<String,Object> attribute)
    {
        if (Objects.nonNull(attribute))
        {
            validatePrimitiveValues(attribute);
            try
            {
                return mapper.writeValueAsString(attribute);
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }

    @Override
    public Map<String,Object>
    convertToEntityAttribute(String dbData)
    {
        if (Objects.nonNull(dbData))
        {
            try
            {
                return
                    mapper.readValue(
                        dbData,
                        mapper
                            .getTypeFactory()
                            .constructMapType(
                                LinkedHashMap.class,
                                String.class,
                                Object.class));
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }

        return null;
    }

    private static void
    validatePrimitiveValues(Map<String,Object> attribute)
    {
        for (Map.Entry<String,Object> entry : attribute.entrySet())
        {
            Object value = entry.getValue();

            if (Objects.nonNull(value) && !isJsonPrimitive(value))
                throw
                    new IllegalArgumentException(
                        "Value for key \""
                        + entry.getKey()
                        + "\" is of type "
                        + value.getClass().getName()
                        + "; only JSON primitives (String, Number, Boolean, null)"
                        + " are allowed. Convert complex values to String before"
                        + " putting them in the map.");
        }
    }

    private static boolean
    isJsonPrimitive(Object value)
    {
        return
            value instanceof String ||
            value instanceof Number ||
            value instanceof Boolean;
    }
}

//////////////////////////////////////////////////////////////////////////////
