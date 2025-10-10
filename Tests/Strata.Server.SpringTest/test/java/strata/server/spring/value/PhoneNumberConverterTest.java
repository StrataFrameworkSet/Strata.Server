//////////////////////////////////////////////////////////////////////////////
// PhoneNumberConverterTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import strata.foundation.core.collection.Pair;
import strata.foundation.core.value.PhoneNumber;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("CommitStage")
public
class PhoneNumberConverterTest
{
    private PhoneNumberConverter subject;

    public static Stream<Pair<PhoneNumber,String>>
    phoneNumbers()
    {
        return
            Stream.of(
                Pair.create(new PhoneNumber("2355551234"),"2355551234"),
                Pair.create(new PhoneNumber("235-555-1234"),"2355551234"),
                Pair.create(new PhoneNumber("235 555 1234"),"2355551234"),
                Pair.create(new PhoneNumber("235.555.1234"),"2355551234"),
                Pair.create(new PhoneNumber("(235)5551234"),"2355551234"),
                Pair.create(new PhoneNumber("(235)555-1234"),"2355551234"),
                Pair.create(new PhoneNumber("(235)555.1234"),"2355551234"),
                Pair.create(new PhoneNumber("(235) 555 1234"),"2355551234"),
                Pair.create(new PhoneNumber("12355551234"),"12355551234"),
                Pair.create(new PhoneNumber("1-235-555-1234"),"12355551234"),
                Pair.create(new PhoneNumber("1 235 555 1234"),"12355551234"),
                Pair.create(new PhoneNumber("1.235.555.1234"),"12355551234"),
                Pair.create(new PhoneNumber("1(235)5551234"),"12355551234"),
                Pair.create(new PhoneNumber("1(235)555-1234"),"12355551234"),
                Pair.create(new PhoneNumber("1(235)555.1234"),"12355551234"),
                Pair.create(new PhoneNumber("1(235) 555 1234"),"12355551234"));
    }

    @BeforeEach
    public void
    setUp() { subject = new PhoneNumberConverter(); }

    @AfterEach
    public void
    tearDown() { subject = null; }

    @ParameterizedTest
    @MethodSource("phoneNumbers")
    public void
    testConversion(Pair<PhoneNumber,String> input)
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
