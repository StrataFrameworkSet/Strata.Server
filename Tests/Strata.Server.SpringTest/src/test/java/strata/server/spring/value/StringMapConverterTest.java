//////////////////////////////////////////////////////////////////////////////
// StringMapConverterTest.java
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
import strata.foundation.core.value.PersonName;
import strata.foundation.core.value.PhoneNumber;
import strata.foundation.core.value.PostalAddress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("CommitStage")
public
class StringMapConverterTest
{
    private StringMapConverter subject;

    public static Stream<Pair<Map<String,Object>,String>>
    maps()
    {
        return
            Stream.of(
                Pair.of(newMap(),"{}"),
                Pair.of(
                    newMap("name","Alice"),
                    "{\"name\":\"Alice\"}"),
                Pair.of(
                    newMap(
                        "name","Alice",
                        "age",42,
                        "active",true,
                        "note",null),
                    "{\"active\":true,\"age\":42,\"name\":\"Alice\",\"note\":null}"),
                Pair.of(
                    newMap(
                        "c",3,
                        "a",1,
                        "b",2),
                    "{\"a\":1,\"b\":2,\"c\":3}"),
                Pair.of(
                    newMap(
                        "quote","she said \"hello\"",
                        "path","C:\\Users\\test"),
                    "{\"path\":\"C:\\\\Users\\\\test\","
                    + "\"quote\":\"she said \\\"hello\\\"\"}"),
                Pair.of(
                    newMap(
                        "unicode","\u00e9\u00e8\u00ea",
                        "emoji","\ud83d\ude00"),
                    "{\"emoji\":\"\ud83d\ude00\",\"unicode\":\"\u00e9\u00e8\u00ea\"}"));
    }

    private static Map<String,Object>
    newMap(Object... keyValues)
    {
        Map<String,Object> result = new LinkedHashMap<>();

        for (int i = 0; i < keyValues.length; i += 2)
            result.put((String) keyValues[i],keyValues[i + 1]);

        return result;
    }

    @BeforeEach
    public void
    setUp() { subject = new StringMapConverter(); }

    @AfterEach
    public void
    tearDown() { subject = null; }

    @ParameterizedTest
    @MethodSource("maps")
    public void
    testSerialization(Pair<Map<String,Object>,String> input)
    {
        assertEquals(
            input.getSecond(),
            subject.convertToDatabaseColumn(input.getFirst()));
    }

    @ParameterizedTest
    @MethodSource("maps")
    public void
    testRoundTrip(Pair<Map<String,Object>,String> input)
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
    testNullDbDataDeserializesToNull()
    {
        assertNull(subject.convertToEntityAttribute(null));
    }

    @Test
    public void
    testEmptyMapSerializesToEmptyJsonObject()
    {
        assertEquals("{}",subject.convertToDatabaseColumn(new LinkedHashMap<>()));
    }

    @Test
    public void
    testEmptyJsonObjectDeserializesToEmptyMap()
    {
        Map<String,Object> result = subject.convertToEntityAttribute("{}");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void
    testJsonIntegerDeserializesAsInteger()
    {
        Map<String,Object> result = subject.convertToEntityAttribute("{\"n\":42}");

        assertInstanceOf(Integer.class,result.get("n"));
        assertEquals(42,result.get("n"));
    }

    @Test
    public void
    testJsonDecimalDeserializesAsDouble()
    {
        Map<String,Object> result = subject.convertToEntityAttribute("{\"n\":1.5}");

        assertInstanceOf(Double.class,result.get("n"));
        assertEquals(1.5,result.get("n"));
    }

    @Test
    public void
    testJsonBooleanDeserializesAsBoolean()
    {
        Map<String,Object> result = subject.convertToEntityAttribute("{\"b\":true}");

        assertInstanceOf(Boolean.class,result.get("b"));
        assertEquals(true,result.get("b"));
    }

    @Test
    public void
    testJsonStringDeserializesAsString()
    {
        Map<String,Object> result = subject.convertToEntityAttribute("{\"s\":\"x\"}");

        assertInstanceOf(String.class,result.get("s"));
        assertEquals("x",result.get("s"));
    }

    @Test
    public void
    testJsonNullDeserializesAsNullValue()
    {
        Map<String,Object> result = subject.convertToEntityAttribute("{\"v\":null}");

        assertTrue(result.containsKey("v"));
        assertNull(result.get("v"));
    }

    @Test
    public void
    testTopLevelMapIsLinkedHashMap()
    {
        Map<String,Object> result =
            subject.convertToEntityAttribute("{\"a\":1,\"b\":2}");

        assertInstanceOf(LinkedHashMap.class,result);
    }

    @Test
    public void
    testKeyOrderPreservedOnDeserialization()
    {
        Map<String,Object> result =
            subject.convertToEntityAttribute("{\"c\":3,\"a\":1,\"b\":2}");

        assertEquals(Arrays.asList("c","a","b"),new ArrayList<>(result.keySet()));
    }

    @Test
    public void
    testKeysSortedAlphabeticallyOnSerialization()
    {
        Map<String,Object> source = new LinkedHashMap<>();

        source.put("z",1);
        source.put("m",2);
        source.put("a",3);

        assertEquals(
            "{\"a\":3,\"m\":2,\"z\":1}",
            subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testMalformedJsonThrowsRuntimeException()
    {
        assertThrows(
            RuntimeException.class,
            () -> subject.convertToEntityAttribute("{not json"));
    }

    @Test
    public void
    testTruncatedJsonThrowsRuntimeException()
    {
        assertThrows(
            RuntimeException.class,
            () -> subject.convertToEntityAttribute("{\"a\":1"));
    }

    @Test
    public void
    testNonObjectJsonThrowsRuntimeException()
    {
        assertThrows(
            RuntimeException.class,
            () -> subject.convertToEntityAttribute("[1,2,3]"));
    }

    @Test
    public void
    testListValueThrowsIllegalArgumentException()
    {
        Map<String,Object> source = new LinkedHashMap<>();

        source.put("tags",Arrays.asList("red","green"));

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testNestedMapValueThrowsIllegalArgumentException()
    {
        Map<String,Object> source = new LinkedHashMap<>();

        source.put("inner",new LinkedHashMap<>());

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testEmailAddressValueThrowsIllegalArgumentException()
    {
        Map<String,Object> source = new LinkedHashMap<>();

        source.put("email",new EmailAddress("abc@xyz.com"));

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testPhoneNumberValueThrowsIllegalArgumentException()
    {
        Map<String,Object> source = new LinkedHashMap<>();

        source.put("phone",new PhoneNumber("235-555-1234"));

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testPersonNameValueThrowsIllegalArgumentException()
    {
        Map<String,Object> source = new LinkedHashMap<>();

        source.put("name",PersonName.of("Jane","Doe"));

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testPostalAddressValueThrowsIllegalArgumentException()
    {
        Map<String,Object> source = new LinkedHashMap<>();

        source.put(
            "address",
            new PostalAddress("123","Main St","Springfield","IL","US","62704"));

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testCharacterValueThrowsIllegalArgumentException()
    {
        Map<String,Object> source = new LinkedHashMap<>();

        source.put("initial",'A');

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testArbitraryObjectValueThrowsIllegalArgumentException()
    {
        Map<String,Object> source = new LinkedHashMap<>();

        source.put("obj",new Object());

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.convertToDatabaseColumn(source));
    }

    @Test
    public void
    testExceptionMessageIdentifiesOffendingKeyAndType()
    {
        Map<String,Object> source = new LinkedHashMap<>();

        source.put("email",new EmailAddress("abc@xyz.com"));

        IllegalArgumentException ex =
            assertThrows(
                IllegalArgumentException.class,
                () -> subject.convertToDatabaseColumn(source));

        assertTrue(ex.getMessage().contains("email"));
        assertTrue(ex.getMessage().contains(EmailAddress.class.getName()));
    }

    @Test
    public void
    testCallerStringifiedEmailAddressRoundTripsAsString()
    {
        EmailAddress original = new EmailAddress("abc@xyz.com");
        EmailAddressConverter emailConverter = new EmailAddressConverter();

        Map<String,Object> source = new LinkedHashMap<>();

        source.put("email",emailConverter.convertToDatabaseColumn(original));

        Map<String,Object> result =
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(source));

        assertInstanceOf(String.class,result.get("email"));
        assertEquals(
            original,
            emailConverter.convertToEntityAttribute(
                (String) result.get("email")));
    }

    @Test
    public void
    testCallerStringifiedPhoneNumberRoundTripsAsString()
    {
        PhoneNumber original = new PhoneNumber("235-555-1234");
        PhoneNumberConverter phoneConverter = new PhoneNumberConverter();

        Map<String,Object> source = new LinkedHashMap<>();

        source.put("phone",phoneConverter.convertToDatabaseColumn(original));

        Map<String,Object> result =
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(source));

        assertInstanceOf(String.class,result.get("phone"));
        assertEquals(
            original,
            phoneConverter.convertToEntityAttribute(
                (String) result.get("phone")));
    }

    @Test
    public void
    testCallerStringifiedPersonNameRoundTripsAsString()
    {
        PersonName original = PersonName.of("Dr","Jane","Q","Doe","Jr");
        PersonNameConverter nameConverter = new PersonNameConverter();

        Map<String,Object> source = new LinkedHashMap<>();

        source.put("name",nameConverter.convertToDatabaseColumn(original));

        Map<String,Object> result =
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(source));

        assertInstanceOf(String.class,result.get("name"));
        assertEquals(
            original,
            nameConverter.convertToEntityAttribute(
                (String) result.get("name")));
    }

    @Test
    public void
    testCallerStringifiedMultipleValueTypesRoundTripAsStrings()
    {
        EmailAddress email = new EmailAddress("jane@example.com");
        PhoneNumber phone = new PhoneNumber("1-235-555-1234");
        PersonName name = PersonName.of("Jane","Doe");

        EmailAddressConverter emailConverter = new EmailAddressConverter();
        PhoneNumberConverter phoneConverter = new PhoneNumberConverter();
        PersonNameConverter nameConverter = new PersonNameConverter();

        Map<String,Object> source = new LinkedHashMap<>();

        source.put("email",emailConverter.convertToDatabaseColumn(email));
        source.put("phone",phoneConverter.convertToDatabaseColumn(phone));
        source.put("name",nameConverter.convertToDatabaseColumn(name));
        source.put("active",true);
        source.put("loginCount",42);

        Map<String,Object> result =
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(source));

        assertInstanceOf(String.class,result.get("email"));
        assertInstanceOf(String.class,result.get("phone"));
        assertInstanceOf(String.class,result.get("name"));
        assertInstanceOf(Boolean.class,result.get("active"));
        assertInstanceOf(Integer.class,result.get("loginCount"));

        assertEquals(
            email,
            emailConverter.convertToEntityAttribute(
                (String) result.get("email")));
        assertEquals(
            phone,
            phoneConverter.convertToEntityAttribute(
                (String) result.get("phone")));
        assertEquals(
            name,
            nameConverter.convertToEntityAttribute(
                (String) result.get("name")));
        assertEquals(true,result.get("active"));
        assertEquals(42,result.get("loginCount"));
    }

    @Test
    public void
    testLongValueRoundTrips()
    {
        Map<String,Object> source = new LinkedHashMap<>();

        source.put("big",10_000_000_000L);

        Map<String,Object> result =
            subject.convertToEntityAttribute(
                subject.convertToDatabaseColumn(source));

        assertEquals(10_000_000_000L,((Number) result.get("big")).longValue());
    }
}

//////////////////////////////////////////////////////////////////////////////
