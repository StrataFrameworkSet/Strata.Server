//////////////////////////////////////////////////////////////////////////////
// StringListConverterTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import strata.foundation.core.collection.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("CommitStage")
public
class StringListConverterTest
{
    private StringListConverter subject;

    public static Stream<Pair<List<String>,String>>
    lists()
    {
        return
            Stream.of(
                Pair.of(Collections.emptyList(),""),
                Pair.of(Collections.singletonList("a"),"a"),
                Pair.of(Arrays.asList("a","b"),"a,b"),
                Pair.of(Arrays.asList("a","b","c"),"a,b,c"),
                Pair.of(
                    Arrays.asList("alpha","bravo","charlie","delta"),
                    "alpha,bravo,charlie,delta"),
                Pair.of(
                    Arrays.asList("hello world","goodbye world"),
                    "hello world,goodbye world"),
                Pair.of(
                    Arrays.asList("red","green","blue","yellow","magenta","cyan"),
                    "red,green,blue,yellow,magenta,cyan"));
    }

    public static Stream<Pair<List<String>,String>>
    unicodeLists()
    {
        return
            Stream.of(
                Pair.of(
                    Arrays.asList("été","écolier"),
                    "été,écolier"),
                Pair.of(
                    Arrays.asList("中文","日本語","한국어"),
                    "中文,日本語,한국어"),
                Pair.of(
                    Arrays.asList("hello","😀","world"),
                    "hello,😀,world"));
    }

    public static Stream<Pair<List<String>,String>>
    escapedLists()
    {
        return
            Stream.of(
                Pair.of(
                    Arrays.asList("a,b","c"),
                    "a\\,b,c"),
                Pair.of(
                    Collections.singletonList("one,two,three"),
                    "one\\,two\\,three"),
                Pair.of(
                    Arrays.asList("a\\b","c"),
                    "a\\\\b,c"),
                Pair.of(
                    Collections.singletonList("\\"),
                    "\\\\"),
                Pair.of(
                    Arrays.asList("a\\,b","c"),
                    "a\\\\\\,b,c"),
                Pair.of(
                    Arrays.asList(",",",,"),
                    "\\,,\\,\\,"));
    }

    @BeforeEach
    public void
    setUp() { subject = new StringListConverter(); }

    @AfterEach
    public void
    tearDown() { subject = null; }

    @ParameterizedTest
    @MethodSource("lists")
    public void
    testSerialization(Pair<List<String>,String> input)
    {
        assertEquals(
            input.getSecond(),
            subject.convertToDatabaseColumn(input.getFirst()));
    }

    @ParameterizedTest
    @MethodSource("lists")
    public void
    testDeserialization(Pair<List<String>,String> input)
    {
        assertEquals(
            input.getFirst(),
            subject.convertToEntityAttribute(input.getSecond()));
    }

    @ParameterizedTest
    @MethodSource("lists")
    public void
    testRoundTrip(Pair<List<String>,String> input)
    {
        assertEquals(
            input.getFirst(),
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(input.getFirst())));
    }

    @ParameterizedTest
    @MethodSource("unicodeLists")
    public void
    testUnicodeSerialization(Pair<List<String>,String> input)
    {
        assertEquals(
            input.getSecond(),
            subject.convertToDatabaseColumn(input.getFirst()));
    }

    @ParameterizedTest
    @MethodSource("unicodeLists")
    public void
    testUnicodeRoundTrip(Pair<List<String>,String> input)
    {
        assertEquals(
            input.getFirst(),
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(input.getFirst())));
    }

    @ParameterizedTest
    @MethodSource("escapedLists")
    public void
    testEscapedSerialization(Pair<List<String>,String> input)
    {
        assertEquals(
            input.getSecond(),
            subject.convertToDatabaseColumn(input.getFirst()));
    }

    @ParameterizedTest
    @MethodSource("escapedLists")
    public void
    testEscapedDeserialization(Pair<List<String>,String> input)
    {
        assertEquals(
            input.getFirst(),
            subject.convertToEntityAttribute(input.getSecond()));
    }

    @ParameterizedTest
    @MethodSource("escapedLists")
    public void
    testEscapedRoundTrip(Pair<List<String>,String> input)
    {
        assertEquals(
            input.getFirst(),
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(input.getFirst())));
    }

    @Test
    public void
    testNullAttributeSerializesToNull()
    {
        assertNull(subject.convertToDatabaseColumn(null));
    }

    @Test
    public void
    testNullDbDataDeserializesToEmptyList()
    {
        List<String> result = subject.convertToEntityAttribute(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void
    testNullAsymmetryBetweenWriteAndRead()
    {
        String written = subject.convertToDatabaseColumn(null);
        List<String> readBack = subject.convertToEntityAttribute(written);

        assertNull(written);
        assertNotNull(readBack);
        assertTrue(readBack.isEmpty());
    }

    @Test
    public void
    testEmptyListSerializesToEmptyString()
    {
        assertEquals("",subject.convertToDatabaseColumn(Collections.emptyList()));
    }

    @Test
    public void
    testEmptyStringDeserializesToEmptyList()
    {
        List<String> result = subject.convertToEntityAttribute("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void
    testWhitespaceOnlyStringDeserializesAsSingletonList()
    {
        assertEquals(
            Collections.singletonList("   "),
            subject.convertToEntityAttribute("   "));
    }

    @Test
    public void
    testSingleElementListSerializesWithoutComma()
    {
        assertEquals(
            "only",
            subject.convertToDatabaseColumn(Collections.singletonList("only")));
    }

    @Test
    public void
    testSingleElementStringDeserializesToSingletonList()
    {
        assertEquals(
            Collections.singletonList("only"),
            subject.convertToEntityAttribute("only"));
    }

    @Test
    public void
    testInsertionOrderPreservedOnSerialization()
    {
        List<String> source = Arrays.asList("z","m","a","q","b");

        assertEquals("z,m,a,q,b",subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testOrderPreservedOnDeserialization()
    {
        List<String> result = subject.convertToEntityAttribute("z,m,a,q,b");

        assertEquals(Arrays.asList("z","m","a","q","b"),result);
    }

    @Test
    public void
    testLeadingCommaProducesEmptyFirstElement()
    {
        assertEquals(
            Arrays.asList("","a","b"),
            subject.convertToEntityAttribute(",a,b"));
    }

    @Test
    public void
    testTrailingCommaProducesEmptyLastElement()
    {
        assertEquals(
            Arrays.asList("a","b",""),
            subject.convertToEntityAttribute("a,b,"));
    }

    @Test
    public void
    testLeadingAndTrailingCommasProduceEmptyEdgeElements()
    {
        assertEquals(
            Arrays.asList("","a","b",""),
            subject.convertToEntityAttribute(",a,b,"));
    }

    @Test
    public void
    testConsecutiveCommasPreserveEmptyElements()
    {
        assertEquals(
            Arrays.asList("a","","b"),
            subject.convertToEntityAttribute("a,,b"));
    }

    @Test
    public void
    testManyConsecutiveCommasPreserveEmptyElements()
    {
        assertEquals(
            Arrays.asList("a","","","","","b"),
            subject.convertToEntityAttribute("a,,,,,b"));
    }

    @Test
    public void
    testOnlyCommasProducesEmptyStringElements()
    {
        assertEquals(
            Arrays.asList("",""),
            subject.convertToEntityAttribute(","));
    }

    @Test
    public void
    testMultipleCommasOnlyProducesEmptyStringElements()
    {
        assertEquals(
            Arrays.asList("","","",""),
            subject.convertToEntityAttribute(",,,"));
    }

    @Test
    public void
    testWhitespaceInStringNotTrimmed()
    {
        assertEquals(
            Arrays.asList("   a","b   "),
            subject.convertToEntityAttribute("   a,b   "));
    }

    @Test
    public void
    testInternalWhitespacePreservedInTokens()
    {
        assertEquals(
            Arrays.asList("hello world","foo bar"),
            subject.convertToEntityAttribute("hello world,foo bar"));
    }

    @Test
    public void
    testSpacesAroundCommasPreservedInTokens()
    {
        List<String> result = subject.convertToEntityAttribute("a , b , c");

        assertEquals(3,result.size());
        assertEquals("a ",result.get(0));
        assertEquals(" b ",result.get(1));
        assertEquals(" c",result.get(2));
    }

    @Test
    public void
    testElementContainingSpaceSerializesVerbatim()
    {
        assertEquals(
            "first element,second element",
            subject.convertToDatabaseColumn(
                Arrays.asList("first element","second element")));
    }

    @Test
    public void
    testSpaceContainingElementsRoundTrip()
    {
        List<String> source = Arrays.asList("first one","second two","third three");

        assertEquals(
            source,
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(source)));
    }

    @Test
    public void
    testElementContainingCommaRoundTripsViaEscape()
    {
        List<String> source = Arrays.asList("a,b","c");
        String written = subject.convertToDatabaseColumn(source);
        List<String> readBack = subject.convertToEntityAttribute(written);

        assertEquals("a\\,b,c",written);
        assertEquals(source,readBack);
    }

    @Test
    public void
    testElementContainingBackslashRoundTripsViaEscape()
    {
        List<String> source = Arrays.asList("a\\b","c");
        String written = subject.convertToDatabaseColumn(source);
        List<String> readBack = subject.convertToEntityAttribute(written);

        assertEquals("a\\\\b,c",written);
        assertEquals(source,readBack);
    }

    @Test
    public void
    testElementContainingLiteralBackslashCommaRoundTrips()
    {
        List<String> source = Arrays.asList("a\\,b","c");
        String written = subject.convertToDatabaseColumn(source);
        List<String> readBack = subject.convertToEntityAttribute(written);

        assertEquals("a\\\\\\,b,c",written);
        assertEquals(source,readBack);
    }

    @Test
    public void
    testEmptyStringElementRoundTripsInMiddle()
    {
        List<String> source = Arrays.asList("a","","b");
        String written = subject.convertToDatabaseColumn(source);
        List<String> readBack = subject.convertToEntityAttribute(written);

        assertEquals("a,,b",written);
        assertEquals(source,readBack);
    }

    @Test
    public void
    testEmptyStringElementRoundTripsAtStart()
    {
        List<String> source = Arrays.asList("","a","b");
        String written = subject.convertToDatabaseColumn(source);
        List<String> readBack = subject.convertToEntityAttribute(written);

        assertEquals(",a,b",written);
        assertEquals(source,readBack);
    }

    @Test
    public void
    testEmptyStringElementRoundTripsAtEnd()
    {
        List<String> source = Arrays.asList("a","b","");
        String written = subject.convertToDatabaseColumn(source);
        List<String> readBack = subject.convertToEntityAttribute(written);

        assertEquals("a,b,",written);
        assertEquals(source,readBack);
    }

    @Test
    public void
    testMultipleEmptyStringElementsRoundTrip()
    {
        List<String> source = Arrays.asList("","","");
        String written = subject.convertToDatabaseColumn(source);
        List<String> readBack = subject.convertToEntityAttribute(written);

        assertEquals(",,",written);
        assertEquals(source,readBack);
    }

    @Test
    public void
    testNullElementSerializesAsEmptyString()
    {
        List<String> source = Arrays.asList("a",null,"b");

        assertEquals("a,,b",subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testSingleNullElementSerializesAsEmptyString()
    {
        assertEquals(
            "",
            subject.convertToDatabaseColumn(Collections.singletonList(null)));
    }

    @Test
    public void
    testNullElementRoundTripsAsEmptyString()
    {
        List<String> source = Arrays.asList("a",null,"b");
        List<String> readBack =
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(source));

        assertEquals(Arrays.asList("a","","b"),readBack);
    }

    @Test
    public void
    testSingletonNullListRoundTripsAsEmptyList()
    {
        List<String> readBack =
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(
                    Collections.singletonList(null)));

        assertTrue(readBack.isEmpty());
    }

    @Test
    public void
    testDeserializedListIsMutable()
    {
        List<String> result = subject.convertToEntityAttribute("a,b,c");

        result.add("d");

        assertEquals(Arrays.asList("a","b","c","d"),result);
    }

    @Test
    public void
    testDeserializedListIsArrayList()
    {
        List<String> result = subject.convertToEntityAttribute("a,b,c");

        assertEquals(ArrayList.class,result.getClass());
    }

    @Test
    public void
    testEmptyListIsAlsoArrayList()
    {
        List<String> result = subject.convertToEntityAttribute(null);

        assertEquals(ArrayList.class,result.getClass());
    }

    @Test
    public void
    testWorksWithAnyListImplementation()
    {
        List<String> source = new LinkedList<>();

        source.add("a");
        source.add("b");

        assertEquals("a,b",subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testLargeListSerializes()
    {
        List<String> source = new ArrayList<>();

        for (int i = 0; i < 1000; i++)
            source.add("item" + i);

        String written = subject.convertToDatabaseColumn(source);
        List<String> readBack = subject.convertToEntityAttribute(written);

        assertEquals(1000,readBack.size());
        assertEquals("item0",readBack.get(0));
        assertEquals("item999",readBack.get(999));
    }

    @Test
    public void
    testTabsInsideElementsPreservedOnSerialization()
    {
        List<String> source = Arrays.asList("a\tb","c\td");

        assertEquals(
            "a\tb,c\td",
            subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testNewlinesInsideElementsPreservedOnSerialization()
    {
        List<String> source = Arrays.asList("line1\nline2","single");

        assertEquals(
            "line1\nline2,single",
            subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testTabsAndNewlinesRoundTrip()
    {
        List<String> source = Arrays.asList("a\tb","line1\nline2");
        List<String> readBack =
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(source));

        assertEquals(source,readBack);
    }
}

//////////////////////////////////////////////////////////////////////////////
