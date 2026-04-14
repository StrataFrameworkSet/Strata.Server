/// ///////////////////////////////////////////////////////////////////////////
// CurrencyUnitConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.money;

import jakarta.persistence.AttributeConverter;
import strata.foundation.core.money.BasicCurrencyUnit;

import javax.money.CurrencyUnit;
import java.util.Objects;

public
class CurrencyUnitConverter
    implements AttributeConverter<CurrencyUnit,String>
{
    @Override
    public String
    convertToDatabaseColumn(CurrencyUnit attribute)
    {
        return
            Objects.nonNull(attribute)
                ? attribute.getCurrencyCode()
                : null;
    }

    @Override
    public CurrencyUnit
    convertToEntityAttribute(String dbData)
    {
        return
            Objects.nonNull(dbData)
                ? BasicCurrencyUnit.of(dbData)
                : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
