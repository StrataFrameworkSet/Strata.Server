//////////////////////////////////////////////////////////////////////////////
// PersonNameConverterTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import strata.foundation.core.container.Pair;
import strata.foundation.core.value.EmailAddress;
import strata.foundation.core.value.PersonName;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("CommitStage")
public
class EmailAddressConverterTest
{
    private EmailAddressConverter subject;

    public static Stream<Pair<EmailAddress,String>>
    emails()
    {
        return
            Stream.of(
                Pair.create(new EmailAddress("abc@xyz.com"),"abc@xyz.com"),
                Pair.create(new EmailAddress("abc.def@xxyyzz.org"),"abc.def@xxyyzz.org"),
                Pair.create(new EmailAddress("john.liebenau@ayuda.software"),"john.liebenau@ayuda.software"));
    }

    @BeforeEach
    public void
    setUp() { subject = new EmailAddressConverter(); }

    @AfterEach
    public void
    tearDown() { subject = null; }

    @ParameterizedTest
    @MethodSource("emails")
    public void
    testConversion(Pair<EmailAddress,String> input)
    {
        assertEquals(
            input.getSecond(),
            subject.convertToDatabaseColumn(input.getFirst()));
        assertEquals(
            input.getFirst(),
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(input.getFirst())));
    }
}

//////////////////////////////////////////////////////////////////////////////
