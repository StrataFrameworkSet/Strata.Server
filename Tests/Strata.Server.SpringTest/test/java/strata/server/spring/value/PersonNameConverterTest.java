//////////////////////////////////////////////////////////////////////////////
// PersonNameConverterTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import strata.foundation.core.collection.Pair;
import strata.foundation.core.value.PersonName;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("CommitStage")
public
class PersonNameConverterTest
{
    private PersonNameConverter subject;

    public static Stream<Pair<PersonName,String>>
    names()
    {
        return
            Stream.of(
                Pair.of(new PersonName("James","Erich","Liebenau, Draughn"),"Liebenau_ Draughn,James,Erich,***,***"),
                Pair.of(new PersonName("John","Friedrich","Liebenau"),"Liebenau,John,Friedrich,***,***"),
                Pair.of(new PersonName("Dr","Ayham",null,"Al-Zoebi",null),"Al-Zoebi,Ayham,***,Dr,***"),
                Pair.of(new PersonName("Aghyan","Al-Zuabi"),"Al-Zuabi,Aghyan,***,***,***"));
    }

    @BeforeEach
    public void
    setUp() { subject = new PersonNameConverter(); }

    @AfterEach
    public void
    tearDown() { subject = null; }

    @ParameterizedTest
    @MethodSource("names")
    public void
    testConversion(Pair<PersonName,String> input)
    {
        assertEquals(
            input.getSecond(),
            subject.convertToDatabaseColumn(input.getFirst()));
        assertEquals(
            input.getFirst(),
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(input.getFirst())));
    }

    @Test
    public void
    testOldFormat()
    {
        PersonName name = new PersonName("John","Friedrich","Liebenau");
        String oldFormat = "Liebenau,John,Friedrich,***";

        assertEquals(name, subject.convertToEntityAttribute(oldFormat));
    }
}

//////////////////////////////////////////////////////////////////////////////
