//////////////////////////////////////////////////////////////////////////////
// SingleCurrencyMoneyConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.money;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import strata.foundation.core.money.Money;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;
import java.util.Objects;

@Converter
public abstract
class SingleCurrencyMoneyConverter
    implements AttributeConverter<Money,BigDecimal>
{
    private final CurrencyUnit currency;

    protected
    SingleCurrencyMoneyConverter(CurrencyUnit currency)
    {
        this.currency = currency;
    }

    @Override
    public BigDecimal
    convertToDatabaseColumn(Money attribute)
    {
        if (Objects.isNull(attribute))
            return null;

        if (!attribute.getCurrency().equals(currency))
            throw
                new IllegalArgumentException(
                    "Attribute currency: " + attribute.getCurrency() + " does not match currency: " + currency);

            return
                attribute
                    .getNumber()
                    .numberValue(BigDecimal.class);
    }

    @Override
    public Money
    convertToEntityAttribute(BigDecimal dbData)
    {
        return
            Objects.nonNull(dbData)
                ? Money.of(currency,dbData)
                : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
