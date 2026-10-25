//////////////////////////////////////////////////////////////////////////////
// StringSetConverterTest.java
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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("CommitStage")
public
class StringSetConverterTest
{
    private StringSetConverter subject;

    public static Stream<Pair<Set<String>,String>>
    sets()
    {
        return
            Stream.of(
                Pair.of(Collections.emptySet(),""),
                Pair.of(linkedHashSet("a"),"a"),
                Pair.of(linkedHashSet("a","b"),"a,b"),
                Pair.of(linkedHashSet("a","b","c"),"a,b,c"),
                Pair.of(
                    linkedHashSet("alpha","bravo","charlie","delta"),
                    "alpha,bravo,charlie,delta"),
                Pair.of(
                    linkedHashSet("hello world","goodbye world"),
                    "hello world,goodbye world"),
                Pair.of(
                    linkedHashSet("red","green","blue","yellow","magenta","cyan"),
                    "red,green,blue,yellow,magenta,cyan"));
    }

    public static Stream<Pair<Set<String>,String>>
    unicodeSets()
    {
        return
            Stream.of(
                Pair.of(
                    linkedHashSet("été","écolier"),
                    "été,écolier"),
                Pair.of(
                    linkedHashSet("中文","日本語","한국어"),
                    "中文,日本語,한국어"),
                Pair.of(
                    linkedHashSet("hello","😀","world"),
                    "hello,😀,world"));
    }

    public static Stream<Pair<Set<String>,String>>
    escapedSets()
    {
        return
            Stream.of(
                Pair.of(
                    linkedHashSet("a,b","c"),
                    "a\\,b,c"),
                Pair.of(
                    linkedHashSet("one,two,three"),
                    "one\\,two\\,three"),
                Pair.of(
                    linkedHashSet("a\\b","c"),
                    "a\\\\b,c"),
                Pair.of(
                    linkedHashSet("\\"),
                    "\\\\"),
                Pair.of(
                    linkedHashSet("a\\,b","c"),
                    "a\\\\\\,b,c"),
                Pair.of(
                    linkedHashSet(",",",,"),
                    "\\,,\\,\\,"));
    }

    private static LinkedHashSet<String>
    linkedHashSet(String... elements)
    {
        return new LinkedHashSet<>(Arrays.asList(elements));
    }

    @BeforeEach
    public void
    setUp() { subject = new StringSetConverter(); }

    @AfterEach
    public void
    tearDown() { subject = null; }

    @ParameterizedTest
    @MethodSource("sets")
    public void
    testSerialization(Pair<Set<String>,String> input)
    {
        assertEquals(
            input.getSecond(),
            subject.convertToDatabaseColumn(input.getFirst()));
    }

    @ParameterizedTest
    @MethodSource("sets")
    public void
    testDeserialization(Pair<Set<String>,String> input)
    {
        assertEquals(
            input.getFirst(),
            subject.convertToEntityAttribute(input.getSecond()));
    }

    @ParameterizedTest
    @MethodSource("sets")
    public void
    testRoundTrip(Pair<Set<String>,String> input)
    {
        assertEquals(
            input.getFirst(),
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(input.getFirst())));
    }

    @ParameterizedTest
    @MethodSource("unicodeSets")
    public void
    testUnicodeSerialization(Pair<Set<String>,String> input)
    {
        assertEquals(
            input.getSecond(),
            subject.convertToDatabaseColumn(input.getFirst()));
    }

    @ParameterizedTest
    @MethodSource("unicodeSets")
    public void
    testUnicodeRoundTrip(Pair<Set<String>,String> input)
    {
        assertEquals(
            input.getFirst(),
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(input.getFirst())));
    }

    @ParameterizedTest
    @MethodSource("escapedSets")
    public void
    testEscapedSerialization(Pair<Set<String>,String> input)
    {
        assertEquals(
            input.getSecond(),
            subject.convertToDatabaseColumn(input.getFirst()));
    }

    @ParameterizedTest
    @MethodSource("escapedSets")
    public void
    testEscapedDeserialization(Pair<Set<String>,String> input)
    {
        assertEquals(
            input.getFirst(),
            subject.convertToEntityAttribute(input.getSecond()));
    }

    @ParameterizedTest
    @MethodSource("escapedSets")
    public void
    testEscapedRoundTrip(Pair<Set<String>,String> input)
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
    testNullDbDataDeserializesToEmptySet()
    {
        Set<String> result = subject.convertToEntityAttribute(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void
    testNullAsymmetryBetweenWriteAndRead()
    {
        String written = subject.convertToDatabaseColumn(null);
        Set<String> readBack = subject.convertToEntityAttribute(written);

        assertNull(written);
        assertNotNull(readBack);
        assertTrue(readBack.isEmpty());
    }

    @Test
    public void
    testEmptySetSerializesToEmptyString()
    {
        assertEquals("",subject.convertToDatabaseColumn(Collections.emptySet()));
    }

    @Test
    public void
    testEmptyStringDeserializesToEmptySet()
    {
        Set<String> result = subject.convertToEntityAttribute("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void
    testWhitespaceOnlyStringDeserializesAsSingletonSet()
    {
        assertEquals(
            linkedHashSet("   "),
            subject.convertToEntityAttribute("   "));
    }

    @Test
    public void
    testSingleElementSetSerializesWithoutComma()
    {
        assertEquals(
            "only",
            subject.convertToDatabaseColumn(Collections.singleton("only")));
    }

    @Test
    public void
    testSingleElementStringDeserializesToSingletonSet()
    {
        assertEquals(
            linkedHashSet("only"),
            subject.convertToEntityAttribute("only"));
    }

    @Test
    public void
    testTreeSetInputSerializesInNaturalOrder()
    {
        Set<String> source = new TreeSet<>(Arrays.asList("z","m","a","q","b"));

        assertEquals("a,b,m,q,z",subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testLinkedHashSetSerializesInInsertionOrder()
    {
        LinkedHashSet<String> source = new LinkedHashSet<>();

        source.add("z");
        source.add("m");
        source.add("a");
        source.add("q");
        source.add("b");

        assertEquals("z,m,a,q,b",subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testDeserializationReturnsInsertionOrderedSet()
    {
        Set<String> result = subject.convertToEntityAttribute("z,m,a,q,b");

        assertEquals(
            Arrays.asList("z","m","a","q","b"),
            new ArrayList<>(result));
    }

    @Test
    public void
    testLeadingCommaProducesEmptyElement()
    {
        assertEquals(
            linkedHashSet("","a","b"),
            subject.convertToEntityAttribute(",a,b"));
    }

    @Test
    public void
    testTrailingCommaProducesEmptyElement()
    {
        assertEquals(
            linkedHashSet("","a","b"),
            subject.convertToEntityAttribute("a,b,"));
    }

    @Test
    public void
    testLeadingAndTrailingCommasProduceSingleEmptyElement()
    {
        assertEquals(
            linkedHashSet("","a","b"),
            subject.convertToEntityAttribute(",a,b,"));
    }

    @Test
    public void
    testConsecutiveCommasProduceSingleEmptyElement()
    {
        assertEquals(
            linkedHashSet("","a","b"),
            subject.convertToEntityAttribute("a,,b"));
    }

    @Test
    public void
    testManyConsecutiveCommasProduceSingleEmptyElement()
    {
        assertEquals(
            linkedHashSet("","a","b"),
            subject.convertToEntityAttribute("a,,,,,b"));
    }

    @Test
    public void
    testOnlyCommasProducesSingletonEmptyStringSet()
    {
        Set<String> result = subject.convertToEntityAttribute(",");

        assertEquals(1,result.size());
        assertTrue(result.contains(""));
    }

    @Test
    public void
    testMultipleCommasOnlyProducesSingletonEmptyStringSet()
    {
        Set<String> result = subject.convertToEntityAttribute(",,,");

        assertEquals(1,result.size());
        assertTrue(result.contains(""));
    }

    @Test
    public void
    testWhitespaceInStringNotTrimmed()
    {
        assertEquals(
            linkedHashSet("   a","b   "),
            subject.convertToEntityAttribute("   a,b   "));
    }

    @Test
    public void
    testInternalWhitespacePreservedInTokens()
    {
        assertEquals(
            linkedHashSet("hello world","foo bar"),
            subject.convertToEntityAttribute("hello world,foo bar"));
    }

    @Test
    public void
    testSpacesAroundCommasPreservedInTokens()
    {
        Set<String> result = subject.convertToEntityAttribute("a , b , c");

        assertEquals(linkedHashSet("a "," b "," c"),result);
    }

    @Test
    public void
    testElementContainingSpaceSerializesVerbatim()
    {
        assertEquals(
            "first element,second element",
            subject.convertToDatabaseColumn(
                linkedHashSet("first element","second element")));
    }

    @Test
    public void
    testSpaceContainingElementsRoundTrip()
    {
        Set<String> source = linkedHashSet("first one","second two","third three");

        assertEquals(
            source,
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(source)));
    }

    @Test
    public void
    testElementContainingCommaRoundTripsViaEscape()
    {
        Set<String> source = linkedHashSet("a,b","c");
        String written = subject.convertToDatabaseColumn(source);
        Set<String> readBack = subject.convertToEntityAttribute(written);

        assertEquals("a\\,b,c",written);
        assertEquals(source,readBack);
    }

    @Test
    public void
    testElementContainingBackslashRoundTripsViaEscape()
    {
        Set<String> source = linkedHashSet("a\\b","c");
        String written = subject.convertToDatabaseColumn(source);
        Set<String> readBack = subject.convertToEntityAttribute(written);

        assertEquals("a\\\\b,c",written);
        assertEquals(source,readBack);
    }

    @Test
    public void
    testElementContainingLiteralBackslashCommaRoundTrips()
    {
        Set<String> source = linkedHashSet("a\\,b","c");
        String written = subject.convertToDatabaseColumn(source);
        Set<String> readBack = subject.convertToEntityAttribute(written);

        assertEquals("a\\\\\\,b,c",written);
        assertEquals(source,readBack);
    }

    @Test
    public void
    testEmptyStringElementRoundTrips()
    {
        Set<String> source = linkedHashSet("","a","b");
        String written = subject.convertToDatabaseColumn(source);
        Set<String> readBack = subject.convertToEntityAttribute(written);

        assertEquals(",a,b",written);
        assertEquals(source,readBack);
    }

    @Test
    public void
    testSingletonEmptyStringSetRoundTripsAsEmptySet()
    {
        Set<String> source = linkedHashSet("");

        assertEquals("",subject.convertToDatabaseColumn(source));
        assertTrue(
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(source)).isEmpty());
    }

    @Test
    public void
    testDuplicateInputStringsAreCollapsedByDeserialization()
    {
        Set<String> result = subject.convertToEntityAttribute("a,b,a,c,b,a");

        assertEquals(linkedHashSet("a","b","c"),result);
    }

    @Test
    public void
    testManyDuplicatesCollapseToSingleElement()
    {
        Set<String> result = subject.convertToEntityAttribute("x,x,x,x,x");

        assertEquals(linkedHashSet("x"),result);
    }

    @Test
    public void
    testHashSetWithNullElementThrowsNullPointerException()
    {
        HashSet<String> source = new HashSet<>();

        source.add("a");
        source.add(null);

        assertThrows(
            NullPointerException.class,
            () -> subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testLinkedHashSetWithNullElementThrowsNullPointerException()
    {
        LinkedHashSet<String> source = new LinkedHashSet<>();

        source.add("a");
        source.add(null);

        assertThrows(
            NullPointerException.class,
            () -> subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testLinkedHashSetAcceptsNullButConverterStillThrowsOnWrite()
    {
        LinkedHashSet<String> source = new LinkedHashSet<>();

        source.add("a");
        source.add(null);

        assertTrue(source.contains(null));
        assertThrows(
            NullPointerException.class,
            () -> subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testDeserializedSetIsMutable()
    {
        Set<String> result = subject.convertToEntityAttribute("a,b,c");

        result.add("d");

        assertEquals(linkedHashSet("a","b","c","d"),result);
    }

    @Test
    public void
    testDeserializedSetIsLinkedHashSet()
    {
        Set<String> result = subject.convertToEntityAttribute("a,b,c");

        assertEquals(LinkedHashSet.class,result.getClass());
    }

    @Test
    public void
    testEmptySetIsAlsoLinkedHashSet()
    {
        Set<String> result = subject.convertToEntityAttribute(null);

        assertEquals(LinkedHashSet.class,result.getClass());
    }

    @Test
    public void
    testWorksWithHashSet()
    {
        HashSet<String> source = new HashSet<>();

        source.add("a");
        source.add("b");
        source.add("c");

        Set<String> readBack =
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(source));

        assertEquals(linkedHashSet("a","b","c"),readBack);
    }

    @Test
    public void
    testWorksWithLinkedHashSet()
    {
        LinkedHashSet<String> source = new LinkedHashSet<>();

        source.add("a");
        source.add("b");
        source.add("c");

        assertEquals("a,b,c",subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testLargeSetSerializes()
    {
        Set<String> source = new TreeSet<>();

        for (int i = 0; i < 1000; i++)
            source.add("item" + i);

        String written = subject.convertToDatabaseColumn(source);
        Set<String> readBack = subject.convertToEntityAttribute(written);

        assertEquals(1000,readBack.size());
        assertTrue(readBack.contains("item0"));
        assertTrue(readBack.contains("item500"));
        assertTrue(readBack.contains("item999"));
    }

    @Test
    public void
    testTabsInsideElementsPreservedOnSerialization()
    {
        Set<String> source = linkedHashSet("a\tb","c\td");

        assertEquals(
            "a\tb,c\td",
            subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testNewlinesInsideElementsPreservedOnSerialization()
    {
        Set<String> source = linkedHashSet("line1\nline2","single");

        assertEquals(
            "line1\nline2,single",
            subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testTabsAndNewlinesRoundTrip()
    {
        Set<String> source = linkedHashSet("a\tb","line1\nline2");
        Set<String> readBack =
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(source));

        assertEquals(source,readBack);
    }

    @Test
    public void
    testOrderOfInputDoesNotAffectEqualityOfOutput()
    {
        LinkedHashSet<String> ascending = new LinkedHashSet<>();

        ascending.add("a");
        ascending.add("b");
        ascending.add("c");

        LinkedHashSet<String> descending = new LinkedHashSet<>();

        descending.add("c");
        descending.add("b");
        descending.add("a");

        Set<String> readA =
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(ascending));
        Set<String> readB =
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(descending));

        assertEquals(readA,readB);
    }

    @Test
    public void
    testUnorderedInputSerializationDiffersByIterationOrder()
    {
        LinkedHashSet<String> source = new LinkedHashSet<>();

        source.add("c");
        source.add("b");
        source.add("a");

        List<String> expectedOrder = Arrays.asList("c","b","a");
        String written = subject.convertToDatabaseColumn(source);

        assertEquals(String.join(",",expectedOrder),written);
    }
}

//////////////////////////////////////////////////////////////////////////////
