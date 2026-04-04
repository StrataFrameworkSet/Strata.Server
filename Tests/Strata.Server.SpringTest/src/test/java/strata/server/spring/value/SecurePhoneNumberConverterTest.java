//////////////////////////////////////////////////////////////////////////////
// SecurePhoneNumberConverterTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import strata.foundation.core.collection.Pair;
import strata.foundation.core.value.PhoneNumber;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("CommitStage")
public
class SecurePhoneNumberConverterTest
{
    private SecurePhoneNumberConverter subject;

    public static Stream<Pair<PhoneNumber,String>>
    phoneNumbers()
    {
        return
            Stream.of(
                Pair.of(new PhoneNumber("2355551234"),"2355551234"),
                Pair.of(new PhoneNumber("235-555-1234"),"235-555-1234"),
                Pair.of(new PhoneNumber("235 555 1234"),"235 555 1234"),
                Pair.of(new PhoneNumber("235.555.1234"),"235.555.1234"),
                Pair.of(new PhoneNumber("(235)5551234"),"(235)5551234"),
                Pair.of(new PhoneNumber("(235)555-1234"),"(235)555-1234"),
                Pair.of(new PhoneNumber("(235)555.1234"),"(235)555.1234"),
                Pair.of(new PhoneNumber("(235) 555 1234"),"(235) 555 1234"),
                Pair.of(new PhoneNumber("12355551234"),"12355551234"),
                Pair.of(new PhoneNumber("1-235-555-1234"),"1-235-555-1234"),
                Pair.of(new PhoneNumber("1 235 555 1234"),"1 235 555 1234"),
                Pair.of(new PhoneNumber("1.235.555.1234"),"1.235.555.1234"),
                Pair.of(new PhoneNumber("1(235)5551234"),"1(235)5551234"),
                Pair.of(new PhoneNumber("1(235)555-1234"),"1(235)555-1234"),
                Pair.of(new PhoneNumber("1(235)555.1234"),"1(235)555.1234"),
                Pair.of(new PhoneNumber("1(235) 555 1234"),"1(235) 555 1234"));
    }

    @BeforeEach
    public void
    setUp() { subject = new SecurePhoneNumberConverter(); }

    @AfterEach
    public void
    tearDown() { subject = null; }

    @ParameterizedTest
    @MethodSource("phoneNumbers")
    public void
    testConversion(Pair<PhoneNumber,String> input)
    {
        String encrypted = subject.convertToDatabaseColumn(input.getFirst());

        assertNotNull(encrypted);
        assertNotEquals(
            input.getSecond(),
            encrypted,
            "encrypted value should differ from plain text");
        assertEquals(
            input.getFirst(),
            subject.convertToEntityAttribute(encrypted));
    }

    @Test
    public void
    testNullConversion()
    {
        assertNull(subject.convertToDatabaseColumn(null));
        assertNull(subject.convertToEntityAttribute(null));
    }
}

//////////////////////////////////////////////////////////////////////////////

