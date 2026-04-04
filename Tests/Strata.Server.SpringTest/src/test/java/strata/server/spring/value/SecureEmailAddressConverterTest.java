//////////////////////////////////////////////////////////////////////////////
// SecureEmailAddressConverterTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import strata.foundation.core.collection.Pair;
import strata.foundation.core.value.EmailAddress;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("CommitStage")
public
class SecureEmailAddressConverterTest
{
    private SecureEmailAddressConverter subject;

    public static Stream<Pair<EmailAddress,String>>
    emails()
    {
        return
            Stream.of(
                Pair.of(new EmailAddress("abc@xyz.com"),"abc@xyz.com"),
                Pair.of(new EmailAddress("abc.def@xxyyzz.org"),"abc.def@xxyyzz.org"),
                Pair.of(new EmailAddress("john.liebenau@ayuda.software"),"john.liebenau@ayuda.software"));
    }

    @BeforeEach
    public void
    setUp() { subject = new SecureEmailAddressConverter(); }

    @AfterEach
    public void
    tearDown() { subject = null; }

    @ParameterizedTest
    @MethodSource("emails")
    public void
    testConversion(Pair<EmailAddress,String> input)
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

