//////////////////////////////////////////////////////////////////////////////
// SecurePersonNameConverterTest.java
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

import static org.junit.jupiter.api.Assertions.*;

@Tag("CommitStage")
public
class SecurePersonNameConverterTest
{
    private SecurePersonNameConverter subject;

    public static Stream<Pair<PersonName,String>>
    names()
    {
        return
            Stream.of(
                // Only required fields: firstName, lastName
                Pair.of(new PersonName("Aghyan","Al-Zuabi"),"Al-Zuabi,Aghyan,***,***,***"),

                // With middleName only
                Pair.of(new PersonName("John","Friedrich","Liebenau"),"Liebenau,John,Friedrich,***,***"),

                // With title only
                Pair.of(new PersonName("Dr","Michael",null,"Smith",null),"Smith,Michael,***,Dr,***"),
                Pair.of(new PersonName("Prof","Alice",null,"Johnson",null),"Johnson,Alice,***,Prof,***"),

                // With suffix only
                Pair.of(new PersonName(null,"Robert",null,"Brown","Jr"),"Brown,Robert,***,***,Jr"),
                Pair.of(new PersonName(null,"William",null,"Davis","III"),"Davis,William,***,***,III"),

                // With title and middleName
                Pair.of(new PersonName("Dr","Ayham","Hassan","Al-Zoebi",null),"Al-Zoebi,Ayham,Hassan,Dr,***"),

                // With title and suffix
                Pair.of(new PersonName("Dr","James",null,"Wilson","Sr"),"Wilson,James,***,Dr,Sr"),

                // With middleName and suffix
                Pair.of(new PersonName(null,"Charles","Edward","Taylor","Jr"),"Taylor,Charles,Edward,***,Jr"),

                // With title, middleName, and suffix (all fields)
                Pair.of(new PersonName("Dr","George","Washington","Adams","III"),"Adams,George,Washington,Dr,III"),
                Pair.of(new PersonName("Prof","Elizabeth","Marie","Martinez","PhD"),"Martinez,Elizabeth,Marie,Prof,PhD"),

                // Special characters: commas in lastName
                Pair.of(new PersonName("James","Erich","Liebenau, Draughn"),"Liebenau_ Draughn,James,Erich,***,***"),

                // Special characters: commas in various fields
                Pair.of(new PersonName("Dr, MD","Sarah",null,"White",null),"White,Sarah,***,Dr_ MD,***"),
                Pair.of(new PersonName(null,"David","Lee, Michael","Garcia",null),"Garcia,David,Lee_ Michael,***,***"),
                Pair.of(new PersonName(null,"Jennifer",null,"Brown, Smith","Jr, Esq"),"Brown_ Smith,Jennifer,***,***,Jr_ Esq"),

                // Edge cases with nulls explicitly
                Pair.of(new PersonName("Dr","Ayham",null,"Al-Zoebi",null),"Al-Zoebi,Ayham,***,Dr,***"),
                Pair.of(new PersonName(null,"Jane",null,"Doe",null),"Doe,Jane,***,***,***"),

                // Hyphenated names
                Pair.of(new PersonName("Mary-Anne","O'Brien"),"O'Brien,Mary-Anne,***,***,***"),
                Pair.of(new PersonName(null,"Jean-Claude","Pierre","Dubois",null),"Dubois,Jean-Claude,Pierre,***,***"),

                // Single character names
                Pair.of(new PersonName("A","B"),"B,A,***,***,***"),
                Pair.of(new PersonName(null,"X","Y","Z",null),"Z,X,Y,***,***"),

                // Long names
                Pair.of(new PersonName("Christopher","Alexander","Wellington-Montgomery"),"Wellington-Montgomery,Christopher,Alexander,***,***"),
                Pair.of(new PersonName("Sir","Bartholomew","Maximilian","Worthington-Smythe","IV"),"Worthington-Smythe,Bartholomew,Maximilian,Sir,IV"),

                // Empty strings in optional fields (title, middleName, suffix)
                Pair.of(new PersonName("","Thomas",null,"Anderson",null),"Anderson,Thomas,***,***,***"),
                Pair.of(new PersonName(null,"Emma","","Wilson",null),"Wilson,Emma,***,***,***"),
                Pair.of(new PersonName(null,"Oliver",null,"Jackson",""),"Jackson,Oliver,***,***,***"),
                Pair.of(new PersonName("","Sophia","","Martinez",""),"Martinez,Sophia,***,***,***"),
                Pair.of(new PersonName("","Liam",null,"Taylor",""),"Taylor,Liam,***,***,***"),
                Pair.of(new PersonName(null,"Isabella","","Brown",""),"Brown,Isabella,***,***,***"),

                // Whitespace-only strings in optional fields
                Pair.of(new PersonName("   ","Noah",null,"Garcia",null),"Garcia,Noah,***,***,***"),
                Pair.of(new PersonName(" \t ","Ava",null,"Rodriguez",null),"Rodriguez,Ava,***,***,***"),
                Pair.of(new PersonName(null,"Ethan","  ","Lopez",null),"Lopez,Ethan,***,***,***"),
                Pair.of(new PersonName(null,"Mia"," \t\n ","Lee",null),"Lee,Mia,***,***,***"),
                Pair.of(new PersonName(null,"James",null,"Walker","   "),"Walker,James,***,***,***"),
                Pair.of(new PersonName(null,"Charlotte",null,"Hall"," \t "),"Hall,Charlotte,***,***,***"),
                Pair.of(new PersonName("  ","Benjamin"," ","Allen"," "),"Allen,Benjamin,***,***,***"),
                Pair.of(new PersonName(" \t ","Amelia",null,"Young","  "),"Young,Amelia,***,***,***"),

                // Mixed empty and whitespace strings
                Pair.of(new PersonName("","Lucas","  ","King",null),"King,Lucas,***,***,***"),
                Pair.of(new PersonName("   ","Harper","","Wright",""),"Wright,Harper,***,***,***"),
                Pair.of(new PersonName("","Mason",null,"Scott"," "),"Scott,Mason,***,***,***"),

                // Whitespace that trims but leaves content
                Pair.of(new PersonName("  Dr  ","Henry",null,"Hill",null),"Hill,Henry,***,Dr,***"),
                Pair.of(new PersonName(null,"Ella","  Ann  ","Green",null),"Green,Ella,Ann,***,***"),
                Pair.of(new PersonName(null,"Alexander",null,"Adams","  Jr  "),"Adams,Alexander,***,***,Jr"),

                // Empty and whitespace with special characters
                Pair.of(new PersonName("","Sofia","","Baker, Mills",""),"Baker_ Mills,Sofia,***,***,***"),
                Pair.of(new PersonName("   ","Daniel",null,"Campbell, Ross"," "),"Campbell_ Ross,Daniel,***,***,***"));
    }

    @BeforeEach
    public void
    setUp() { subject = new SecurePersonNameConverter(); }

    @AfterEach
    public void
    tearDown() { subject = null; }

    @ParameterizedTest
    @MethodSource("names")
    public void
    testConversion(Pair<PersonName,String> input)
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

