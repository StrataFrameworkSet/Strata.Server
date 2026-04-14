/// ///////////////////////////////////////////////////////////////////////////
// NumberValueConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.money;

import jakarta.persistence.AttributeConverter;
import strata.foundation.core.money.BigDecimalValue;

import javax.money.NumberValue;
import java.math.BigDecimal;
import java.util.Objects;

public
class NumberValueConverter
    implements AttributeConverter<NumberValue,BigDecimal>
{
    @Override
    public BigDecimal
    convertToDatabaseColumn(NumberValue attribute)
    {
        return
            Objects.nonNull(attribute)
                ? attribute.numberValue(BigDecimal.class)
                : null;
    }

    @Override
    public NumberValue
    convertToEntityAttribute(BigDecimal dbData)
    {
        return
            Objects.nonNull(dbData)
                ? new BigDecimalValue(dbData)
                : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
