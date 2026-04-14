//////////////////////////////////////////////////////////////////////////////
// MoneyConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.money;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import strata.foundation.core.money.Money;

import java.util.Objects;

@Converter
public
class MoneyConverter
    implements AttributeConverter<Money,String>
{
    @Override
    public String
    convertToDatabaseColumn(Money attribute)
    {
        return
            Objects.nonNull(attribute)
                ? attribute.toString()
                : null;
    }

    @Override
    public Money
    convertToEntityAttribute(String dbData)
    {
        return
            Objects.nonNull(dbData)
                ? new Money(dbData)
                : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
