package no.priv.bang.modeling.modelstore.value;

import static no.priv.bang.modeling.modelstore.value.Values.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static no.priv.bang.modeling.modelstore.services.Propertyset.*;
import static no.priv.bang.modeling.modelstore.value.Propertysets.*;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;

class PropertysetImplTest {

    /**
     * Test creating an empty {@link PropertysetImpl} class and verify
     * the expected empty values for all property types.
     */
    @Test
    void testCreateEmptyPropertyset() {
        var propertyset = new PropertysetImpl();
        assertFalse(propertyset.isNil());

        var boolProperty = propertyset.getBooleanProperty("boolProperty");
        assertEquals(Boolean.valueOf(false), boolProperty);
        var intProperty = propertyset.getLongProperty("intPropName");
        assertEquals(Long.valueOf(0), intProperty);
        var doubleProperty = propertyset.getDoubleProperty("doublePropName");
        assertEquals(Double.valueOf(0), doubleProperty);
        var stringProperty = propertyset.getStringProperty("stringProperty");
        assertEquals("", stringProperty);
        var complexProperty = propertyset.getComplexProperty("complexProperty");
        assertNotEquals(complexProperty, null); // NOSONAR the point here is to test propertyset.equals, so no the arguments should not be swapped
        assertTrue(complexProperty.isNil());
        var reference = propertyset.getReferenceProperty("reference");
        assertNotEquals(reference, null); // NOSONAR the point here is to test propertyset.equals, so no the arguments should not be swapped
        assertTrue(reference.isNil());
    }

    /**
     * Test getting a bool from various property types
     */
    @Test
    void testGetBooleanProperty() {
        var propertyset = new PropertysetImpl();

        var trueValue = Boolean.valueOf(true);
        propertyset.setBooleanProperty("trueValue", trueValue);
        var boolValue = propertyset.getBooleanProperty("trueValue");
        assertEquals(trueValue, boolValue);
        propertyset.setBooleanProperty("alsoTrue", true);
        var alsoTrue = propertyset.getBooleanProperty("alsoTrue");
        assertTrue(alsoTrue);

        // Make sure "id" can't be assigned a boolean.
        assertFalse(propertyset.getBooleanProperty("id"));
        propertyset.setBooleanProperty("id", true);
        assertFalse(propertyset.getBooleanProperty("id"));

        // Set int values and read them back as bool values
        // 0 is false, everything else is true
        var negativeInt = Long.valueOf(-1);
        propertyset.setLongProperty("negativeInt", negativeInt);
        var negativeIntAsBool = propertyset.getBooleanProperty("negativeInt");
        assertEquals(Boolean.valueOf(true), negativeIntAsBool);
        var nullInt = Long.valueOf(0);
        propertyset.setLongProperty("nullInt", nullInt);
        var nullIntAsBool = propertyset.getBooleanProperty("nullInt");
        assertEquals(Boolean.valueOf(false), nullIntAsBool);
        var positiveInt = Long.valueOf(1);
        propertyset.setLongProperty("positiveInt", positiveInt);
        var positiveIntAsBool = propertyset.getBooleanProperty("positiveInt");
        assertEquals(Boolean.valueOf(true), positiveIntAsBool);

        // Set a null value and get a false boolean value back
        propertyset.setBooleanProperty("nullValue", null); // NOSONAR the point here is to test propertyset.equals, so no the arguments should not be swapped
        var nullValue = propertyset.getBooleanProperty("nullValue");
        assertEquals(Boolean.valueOf(false), nullValue);

        // Set float values and read them back as bool values
        // 0.0 is false, everything else is true
        var negativeFloat = Double.valueOf(-1.0);
        propertyset.setDoubleProperty("negativeFloat", negativeFloat);
        var negativeFloatAsBool = propertyset.getBooleanProperty("negativeFloat");
        assertEquals(Boolean.valueOf(true), negativeFloatAsBool);
        var nullFloat = Double.valueOf(0.0);
        propertyset.setDoubleProperty("nullFloat", nullFloat);
        var nullFloatAsBool = propertyset.getBooleanProperty("nullFloat");
        assertEquals(Boolean.valueOf(false), nullFloatAsBool);
        var positiveFloat = Double.valueOf(1.0);
        propertyset.setDoubleProperty("positiveFloat", positiveFloat);
        var positiveFloatAsBool = propertyset.getBooleanProperty("positiveFloat");
        assertEquals(Boolean.valueOf(true), positiveFloatAsBool);

        // Set string values and read them back as bool values
        // "true" is true, everything else is false
        var falseString = "false";
        propertyset.setStringProperty("falseString", falseString);
        var falseStringAsBool = propertyset.getBooleanProperty("falseString");
        assertEquals(Boolean.valueOf(false), falseStringAsBool);
        var trueString = "true";
        propertyset.setStringProperty("trueString", trueString);
        var trueStringAsBool = propertyset.getBooleanProperty("trueString");
        assertEquals(Boolean.valueOf(true), trueStringAsBool);
        var notABoolString = "hey there!";
        propertyset.setStringProperty("notABoolString", notABoolString);
        var notABoolStringAsString = propertyset.getBooleanProperty("notABoolString");
        assertEquals(Boolean.valueOf(false), notABoolStringAsString);
    }

    /**
     * Test getting an integer from various property types
     */
    @Test
    void testGetLongProperty() {
        var propertyset = new PropertysetImpl();
        assertFalse(propertyset.isNil());

        // Verify that a value set as an integer is returned as the same Long value
        var intValue1 = Long.valueOf(1234);
        propertyset.setLongProperty("intValue", intValue1);
        var intValue2 = propertyset.getLongProperty("intValue");
        assertEquals(intValue1, intValue2);
        propertyset.setLongProperty("anotherInt", 42);
        var anotherInt = propertyset.getLongProperty("anotherInt");
        assertEquals(42, anotherInt.longValue());

        // Make sure "id" can't be assigned a long.
        assertEquals(0, propertyset.getLongProperty("id").longValue());
        propertyset.setLongProperty("id", 43);
        assertEquals(0, propertyset.getLongProperty("id").longValue());

        // Set some double values and read them back as Long values
        var doubleValueContainingInt = Double.valueOf(7);
        propertyset.setDoubleProperty("doubleValueContainingInt", doubleValueContainingInt);
        var doubleValueReadBackAsInt1 = propertyset.getLongProperty("doubleValueContainingInt");
        assertEquals(Long.valueOf(7), doubleValueReadBackAsInt1);
        var doubleValueContainingNonInt = Double.valueOf(2.78);
        propertyset.setDoubleProperty("doubleValueReadBackAsInt", doubleValueContainingNonInt);
        var doubleValueReadBackAsInt2 = propertyset.getLongProperty("doubleValueReadBackAsInt");
        assertEquals(Long.valueOf(3), doubleValueReadBackAsInt2);

        // Set a null value and verify that a zero int value is returned
        propertyset.setLongProperty("nullValue", null);
        var nullValue = propertyset.getLongProperty("nullValue");
        assertEquals(Long.valueOf(0), nullValue);

        // Set some string values and read them back as Long values
        var stringValueContainingInt = "11";
        propertyset.setStringProperty("stringValueContainingInt", stringValueContainingInt);
        var stringValueReadBackAsInt1 = propertyset.getLongProperty("stringValueContainingInt");
        assertEquals(Long.valueOf(11), stringValueReadBackAsInt1);
        var stringValueContainingFloat = "2.78";
        propertyset.setStringProperty("stringValueContainingFloat", stringValueContainingFloat);
        var stringValueReadBackAsInt2 = propertyset.getLongProperty("stringValueContainingFloat");
        assertEquals(Long.valueOf(3), stringValueReadBackAsInt2);
        var stringValueNotParsableAsANumber = "not a number";
        propertyset.setStringProperty("stringValueNotParsableAsANumber", stringValueNotParsableAsANumber);
        var stringValueReadBackAsInt3 = propertyset.getLongProperty("stringValueNotParsableAsANumber");
        assertEquals(Long.valueOf(0), stringValueReadBackAsInt3);

        // set boolean values and read them back as integers.
        var falseValue = Boolean.valueOf(false);
        propertyset.setBooleanProperty("falseValue", falseValue);
        var falseAsLong = propertyset.getLongProperty("falseValue");
        assertEquals(Long.valueOf(0), falseAsLong);
        var trueValue = Boolean.valueOf(true);
        propertyset.setBooleanProperty("trueValue", trueValue);
        var trueAsLong = propertyset.getLongProperty("trueValue");
        assertEquals(Long.valueOf(1), trueAsLong);
    }

    /**
     * Test getting a double from various property types
     */
    @Test
    void testGetDoubleProperty() {
        var propertyset = new PropertysetImpl();
        assertFalse(propertyset.isNil());

        // Verify that a value set as a double is returned as the same Double value
        var doubleValue1 = Double.valueOf(3.14);
        propertyset.setDoubleProperty("doubleValue", doubleValue1);
        var doubleValue2 = propertyset.getDoubleProperty("doubleValue");
        assertEquals(doubleValue1, doubleValue2);
        propertyset.setDoubleProperty("anotherDouble", 2.78);
        var anotherDouble = propertyset.getDoubleProperty("anotherDouble");
        assertEquals(2.78, anotherDouble, 0.0);

        // Make sure "id" can't be assigned a double.
        assertEquals(0.0, propertyset.getDoubleProperty("id").doubleValue(), 0.0);
        propertyset.setDoubleProperty("id", 43.0);
        assertEquals(0.0, propertyset.getDoubleProperty("id").doubleValue(), 0.0);

        // Verify that setting a null value will return a zero double value.
        propertyset.setDoubleProperty("nullDoubleValue", null);
        var nullDoubleValue = propertyset.getDoubleProperty("nullDoubleValue");
        assertEquals(Double.valueOf(0.0), nullDoubleValue);

        // Verify that a value set as an int can be returned as a Double containing the same value
        var intValue1 = Long.valueOf(17);
        propertyset.setLongProperty("intValue1", intValue1);
        var doubleValue3 = propertyset.getDoubleProperty("intValue1");
        assertEquals(Double.valueOf(17), doubleValue3);

        // Verify that a float value in a string can be returned as a Double containing the same value.
        var stringValueWithFloat = "3.14";
        propertyset.setStringProperty("stringValueWithFloat", stringValueWithFloat);
        var doubleValue4 = propertyset.getDoubleProperty("stringValueWithFloat");
        assertEquals(Double.valueOf(3.14), doubleValue4);

        // Verify that an int value in a string can be returned as a Double containing the same value
        var stringValueWithInt = "27";
        propertyset.setStringProperty("stringValueWithFloat", stringValueWithInt);
        var doubleValue5 = propertyset.getDoubleProperty("stringValueWithFloat");
        assertEquals(Double.valueOf(27), doubleValue5);

        // Verify that string not parseable as a numeric value can be returned as a Double with value 0.0
        var stringValueNotParsableAsANumber = "This is not a number";
        propertyset.setStringProperty("stringValueNotParsableAsANumber", stringValueNotParsableAsANumber);
        var doubleValue6 = propertyset.getDoubleProperty("stringValueNotParsableAsANumber");
        assertEquals(Double.valueOf(0.0), doubleValue6);

        // set boolean values and read them back as floating point numbers.
        var falseValue = Boolean.valueOf(false);
        propertyset.setBooleanProperty("falseValue", falseValue);
        var falseAsDouble = propertyset.getDoubleProperty("falseValue");
        assertEquals(Double.valueOf(0.0), falseAsDouble);
        var trueValue = Boolean.valueOf(true);
        propertyset.setBooleanProperty("trueValue", trueValue);
        var trueAsDouble = propertyset.getDoubleProperty("trueValue");
        assertEquals(Double.valueOf(1.0), trueAsDouble);
    }

    /**
     * Test getting a string back from various property types.
     */
    @Test
    void testGetStringProperty() {
        var propertyset = new PropertysetImpl();

        // Set a string property and get it back as a string
        var stringValue = "Hello world!";
        propertyset.setStringProperty("stringValue", stringValue);
        var stringValueReadBackFromProperty = propertyset.getStringProperty("stringValue");
        assertEquals(stringValue, stringValueReadBackFromProperty);

        // Set a null string property and verify that it is returned as an empty string
        propertyset.setStringProperty("nullValue", null);
        var nullValue = propertyset.getStringProperty("nullValue");
        assertEquals("", nullValue);

        // Set an int property and read it back as a string
        var intValue = Long.valueOf(42);
        propertyset.setLongProperty("intValue", intValue);
        var intValueAsString = propertyset.getStringProperty("intValue");
        assertEquals("42", intValueAsString);

        // Set a double property and read it back as a string
        var doubleValue = Double.valueOf(37.5);
        propertyset.setDoubleProperty("doubleValue", doubleValue);
        var doubleValueAsString = propertyset.getStringProperty("doubleValue");
        assertEquals("37.5", doubleValueAsString);

        // set boolean values and read them back as floating point numbers.
        var falseValue = Boolean.valueOf(false);
        propertyset.setBooleanProperty("falseValue", falseValue);
        var falseAsString = propertyset.getStringProperty("falseValue");
        assertEquals("false", falseAsString);
        var trueValue = Boolean.valueOf(true);
        propertyset.setBooleanProperty("trueValue", trueValue);
        var trueAsString = propertyset.getStringProperty("trueValue");
        assertEquals("true", trueAsString);
    }


    /**
     * Test getting a complex property back from various property types.
     *
     * Only a property actually set as a complex property should return
     * something other than {@link PropertysetNil#getNil()},
     */
    @Test
    void testGetComplexProperty() {
        var valueCreator = new ValueCreatorProvider();
        var propertyset = valueCreator.newPropertyset(UUID.randomUUID());

        // Set a complex property and retrieve it.
        var point = valueCreator.newPropertyset();
        point.setDoubleProperty("x", 75.3);
        point.setDoubleProperty("y", 145.3);
        propertyset.setComplexProperty("upperLeftCorner", point);
        var upperLeftCorner = propertyset.getComplexProperty("upperLeftCorner");
        assertEquals(Double.valueOf(75.3), upperLeftCorner.getDoubleProperty("x"));
        assertEquals(Double.valueOf(145.3), upperLeftCorner.getDoubleProperty("y"));

        // Set a null complex property value and verify that it results in a nil Propertyset
        propertyset.setComplexProperty("nullValue", null);
        var nullValue = propertyset.getComplexProperty("nullValue");
        assertEquals(getNilPropertyset(), nullValue);

        // Check that accessing a complex property as other property types
        // gives null values (a complex property can't be autoconverted to
        // a different type).
        var upperLeftCornerAsBoolean = propertyset.getBooleanProperty("upperLeftCorner");
        assertEquals(Boolean.valueOf(false), upperLeftCornerAsBoolean);
        var upperLeftCornerAsLong = propertyset.getLongProperty("upperLeftCorner");
        assertEquals(Long.valueOf(0), upperLeftCornerAsLong);
        var upperLeftCornerAsDouble = propertyset.getDoubleProperty("upperLeftCorner");
        assertEquals(Double.valueOf(0.0), upperLeftCornerAsDouble);
        var upperLeftCornerAsString = propertyset.getStringProperty("upperLeftCorner");
        assertEquals("", upperLeftCornerAsString);
        var upperLeftCornerAsReference = propertyset.getReferenceProperty("upperLeftCorner");
        assertEquals(getNilPropertyset(), upperLeftCornerAsReference);

        // Set a some properties with different types and try reading them back as complex properties
        // They all result in a nil complex property (ie. no value conversion).
        propertyset.setBooleanProperty("boolValue", Boolean.valueOf(true));
        assertEquals(getNilPropertyset(), propertyset.getComplexProperty("boolValue"));
        propertyset.setLongProperty("longValue", Long.valueOf(42));
        assertEquals(getNilPropertyset(), propertyset.getComplexProperty("longValue"));
        propertyset.setDoubleProperty("doubleValue", Double.valueOf(2.78));
        assertEquals(getNilPropertyset(), propertyset.getComplexProperty("doubleValue"));
        propertyset.setStringProperty("stringValue", "foo bar");
        assertEquals(getNilPropertyset(), propertyset.getComplexProperty("stringValue"));
        var referencedObject = new PropertysetImpl();
        propertyset.setReferenceProperty("referenceValue", referencedObject);
        assertEquals(getNilPropertyset(), propertyset.getComplexProperty("referenceValue"));

        // Verify deep copy on set
        var otherpropertyset = valueCreator.newPropertyset(UUID.randomUUID());
        otherpropertyset.setComplexProperty("corner", propertyset.getComplexProperty("upperLeftCorner"));
        otherpropertyset.getComplexProperty("corner").setDoubleProperty("x", 77);
        assertEquals(77.0, otherpropertyset.getComplexProperty("corner").getDoubleProperty("x"), 0.0);
        assertEquals(75.3, propertyset.getComplexProperty("upperLeftCorner").getDoubleProperty("x"), 0.0, "Expected original to be unchanged");
    }

    /**
     * Test getting a complex property back from various property types.
     *
     * Only a property actually set as a complex property should return
     * something other than {@link PropertysetNil#getNil()},
     */
    @Test
    void testGetReferenceProperty() {
        var propertyset = new PropertysetImpl();

        // Set an object reference property value and read it back
        var aDifferentPropset = new PropertysetImpl();
        aDifferentPropset.setStringProperty("foo", "bar");
        propertyset.setReferenceProperty("aLinkedObject", aDifferentPropset);
        var aLinkedObject = propertyset.getReferenceProperty("aLinkedObject");
        assertEquals(aDifferentPropset, aLinkedObject);

        // Check that accessing a reference property as other property types
        // gives null values (a reference property can't be autoconverted to
        // a different type).
        var aLinkedObjectAsBoolean = propertyset.getBooleanProperty("aLinkedObject");
        assertEquals(Boolean.valueOf(false), aLinkedObjectAsBoolean);
        var aLinkedObjectAsLong = propertyset.getLongProperty("aLinkedObject");
        assertEquals(Long.valueOf(0), aLinkedObjectAsLong);
        var aLinkedObjectAsDouble = propertyset.getDoubleProperty("aLinkedObject");
        assertEquals(Double.valueOf(0.0), aLinkedObjectAsDouble);
        var aLinkedObjectAsString = propertyset.getStringProperty("aLinkedObject");
        assertEquals("", aLinkedObjectAsString);
        var aLinkedObjectAsComplexProperty = propertyset.getComplexProperty("aLinkedObject");
        assertEquals(getNilPropertyset(), aLinkedObjectAsComplexProperty);

        // Set a some properties with different types and try reading them back as reference properties
        // They all result in a nil reference property (ie. no value conversion).
        propertyset.setBooleanProperty("boolValue", Boolean.valueOf(true));
        assertEquals(getNilPropertyset(), propertyset.getReferenceProperty("boolValue"));
        propertyset.setLongProperty("longValue", Long.valueOf(42));
        assertEquals(getNilPropertyset(), propertyset.getReferenceProperty("longValue"));
        propertyset.setDoubleProperty("doubleValue", Double.valueOf(2.78));
        assertEquals(getNilPropertyset(), propertyset.getReferenceProperty("doubleValue"));
        propertyset.setStringProperty("stringValue", "foo bar");
        assertEquals(getNilPropertyset(), propertyset.getReferenceProperty("stringValue"));
        var complexdObject = new PropertysetImpl();
        propertyset.setComplexProperty("complexValue", complexdObject);
        assertEquals(getNilPropertyset(), propertyset.getReferenceProperty("complexValue"));
    }

    /**
     * Test getting a list property back from various types.
     */
    @Test
    void testGetListProperty() {
        var valueCreator = new ValueCreatorProvider();
        var propertyset = valueCreator.newPropertyset(UUID.randomUUID());

        // Set and get a list value, and verify that its members can be accessed.
        var listValue = newList();
        listValue.add(toBooleanValue(Boolean.valueOf(true)));
        listValue.add(toLongValue(Long.valueOf(42)));
        propertyset.setListProperty("listValue", listValue);
        var retrievedListValue = propertyset.getListProperty("listValue");
        assertEquals(2, retrievedListValue.size());
        assertEquals(Long.valueOf(42), retrievedListValue.get(1).asLong());

        // Expect and empty and unmodifiable list when accessing a property set with a null value
        propertyset.setListProperty("nullValue", null);
        var nullValue = propertyset.getListProperty("nullValue");
        assertEquals(0, nullValue.size());

        // Expect an empty and unmodifiable list when accessing a non-existing property.
        var emptyList = propertyset.getListProperty("noSuchList");
        assertEquals(0, emptyList.size());

        // Expect all non-list properties to result in an empty list when accessing them
        propertyset.setBooleanProperty("boolValue", Boolean.valueOf(true));
        assertEquals(getNil().asList(), propertyset.getListProperty("boolValue"));
        propertyset.setLongProperty("longValue", Long.valueOf(42));
        assertEquals(getNil().asList(), propertyset.getListProperty("longValue"));
        propertyset.setDoubleProperty("doubleValue", Double.valueOf(2.78));
        assertEquals(getNil().asList(), propertyset.getListProperty("doubleValue"));
        propertyset.setStringProperty("stringValue", "foo bar");
        assertEquals(getNil().asList(), propertyset.getListProperty("stringValue"));
        var complexdObject = new PropertysetImpl();
        propertyset.setComplexProperty("complexValue", complexdObject);
        assertEquals(getNil().asList(), propertyset.getListProperty("complexValue"));
        var referencedObject = new PropertysetImpl();
        propertyset.setReferenceProperty("referencedObject", referencedObject);
        assertEquals(getNil().asList(), propertyset.getListProperty("referencedObject"));

        // Verify deep copy on set
        var otherpropertyset = valueCreator.newPropertyset();
        otherpropertyset.setListProperty("listValue", propertyset.getListProperty("listValue"));
        otherpropertyset.getListProperty("listValue").add(378);
        assertEquals(3, otherpropertyset.getListProperty("listValue").size());
        assertEquals(2, propertyset.getListProperty("listValue").size(), "Expected original to be unchanged");
    }

    @Test
    void testEmptyPropertysetEqualsPropertysetNil() {
        var emptypropertyset = new PropertysetImpl();
        var nil = getNilPropertyset();
        assertEquals(emptypropertyset, nil);
        assertEquals(nil, emptypropertyset);
    }

    @Test
    void testGetProperty() {
        var valueCreator = new ValueCreatorProvider();
        var emptypropertyset = valueCreator.newPropertyset();
        Value nosuchproperty = emptypropertyset.getProperty("nosuchproperty");
        assertEquals(getNil(), nosuchproperty);
        var propertyset = valueCreator.newPropertyset();
        propertyset.setProperty("null", null);
        assertEquals(getNilPropertyset(), propertyset.getComplexProperty("null"));

        // Verify deep copy for complex and list properties
        propertyset.setComplexProperty("a", valueCreator.newPropertyset());
        propertyset.getComplexProperty("a").setLongProperty("l", 42);
        propertyset.setListProperty("b", newList());
        propertyset.getListProperty("b").add(345);

        var otherpropertyset = valueCreator.newPropertyset();
        otherpropertyset.setProperty("a", propertyset.getProperty("a"));
        otherpropertyset.getComplexProperty("a").setLongProperty("l", 43);
        assertEquals(43, otherpropertyset.getComplexProperty("a").getLongProperty("l").longValue());
        assertEquals(42, propertyset.getComplexProperty("a").getLongProperty("l").longValue(), "Expected original value to be unchanged");
        otherpropertyset.setProperty("b", propertyset.getProperty("b"));
        otherpropertyset.getListProperty("b").add("wow");
        assertEquals(2, otherpropertyset.getListProperty("b").size());
        assertEquals(1, propertyset.getListProperty("b").size(), "Expected original value to be unchanged");
    }

    /**
     * Corner case unit tests for {@link Propertyset#setProperty(String, Value)}.
     */
    @Test
    void testSetProperty() {
        var valueCreator = new ValueCreatorProvider();

        // Verify that id can't be set
        var idValue = createIdValue(valueCreator);
        var propertyset = valueCreator.newPropertyset();
        propertyset.setProperty(ID_KEY, idValue);
        assertEquals(getNil(), propertyset.getProperty(ID_KEY));
        assertEquals(getNilPropertyset().getId(), propertyset.getId());

        // Verify that aspects can't be set as a property
        var aspectsValue = createAspectsValue(valueCreator);
        propertyset.setProperty(ASPECTS_KEY, aspectsValue);
        assertEquals(getNil(), propertyset.getProperty(ASPECTS_KEY));
        assertFalse(propertyset.hasAspect());
        assertEquals(0, propertyset.getAspects().size());

        // Verify that a regular value can be set and retrieved
        // (reusing the aspectsValue which is a list of references.)
        propertyset.setProperty("reference_list", aspectsValue);
        assertEquals(aspectsValue, propertyset.getProperty("reference_list"));
    }

    @Test
    void testHashCode() {
        var valueCreator = new ValueCreatorProvider();
        var emptypropertyset = new PropertysetImpl();
        assertEquals(31, emptypropertyset.hashCode());
        var id = UUID.fromString("8ce20479-8876-4d84-98a3-c14b53715c5d");
        var propertyset = new PropertysetImpl();
        populatePropertyset(propertyset, valueCreator, id);
        assertEquals(201512373, propertyset.hashCode());
    }

    @Test
    void testEquals() {
        var valueCreator = new ValueCreatorProvider();
        var emptypropertyset = new PropertysetImpl();
        assertEquals(emptypropertyset, emptypropertyset);
        assertNotEquals(emptypropertyset, null); // NOSONAR the point here is to test propertyset.equals, so no the arguments should not be swapped
        assertEquals(emptypropertyset, getNilPropertyset());

        // Compare two identical but propertysets that aren't the same object
        var id = UUID.randomUUID();
        var propertyset1 = new PropertysetImpl();
        populatePropertyset(propertyset1, valueCreator, id);
        var propertyset2 = new PropertysetImpl();
        populatePropertyset(propertyset2, valueCreator, id);
        assertEquals(propertyset1, propertyset2);
        assertEquals(propertyset2, propertyset1);
    }

    @Test
    void testToString() {
        var emptypropertyset = new PropertysetImpl();
        assertEquals("PropertysetImpl [properties={}]", emptypropertyset.toString());
        // No test for a propertyset with properties, because they are written in a non-predictable order
    }

    @Test
    void testCopyConstructor() {
        var valueCreator = new ValueCreatorProvider();
        var id = UUID.randomUUID();
        var propertyset = new PropertysetImpl();
        populatePropertyset(propertyset, valueCreator, id);

        var copy = new PropertysetImpl(propertyset);
        compareOriginalUnchangedByCopyChange(valueCreator, propertyset, copy);
    }

    @Test
    void testCopyConstructorOnPropertysetWithIdAndAspect() {
        var valueCreator = new ValueCreatorProvider();
        var id = UUID.randomUUID();
        var propsetId = UUID.randomUUID();
        var propertyset = valueCreator.newPropertyset(propsetId);
        var aspectId = UUID.randomUUID();
        var aspect = valueCreator.newPropertyset(aspectId);
        propertyset.addAspect(aspect);
        populatePropertyset(propertyset, valueCreator, id);

        var copy = new PropertysetImpl(propertyset);
        compareOriginalUnchangedByCopyChange(valueCreator, propertyset, copy);
    }

    @Test
    void testCopyValues() {
        var valueCreator = new ValueCreatorProvider();
        var propertyset = valueCreator.newPropertyset();
        var referencedPropertysetId = UUID.randomUUID();
        populatePropertyset(propertyset, valueCreator, referencedPropertysetId);

        // Make a copy to compare with afterwards
        var copyOfPropertyset = new PropertysetImpl(propertyset);
        assertNotSame(propertyset, copyOfPropertyset);
        assertEquals(propertyset, copyOfPropertyset);

        // Try copying in a null propertyset
        propertyset.copyValues(null);

        // Expected the propertyset to be unchanged by the null-copying
        assertEquals(propertyset, copyOfPropertyset);

        // Copy from a propertyset with id
        var propertysetWithId = valueCreator.newPropertyset(UUID.randomUUID());
        populatePropertyset(propertysetWithId, valueCreator, referencedPropertysetId);

        // propertysets are not equal because one has an id
        assertNotEquals(propertyset, propertysetWithId);

        // Make a non-id copy of the propertyset with id
        var copyOfPropertysetWithId = valueCreator.newPropertyset();
        copyOfPropertysetWithId.copyValues(propertysetWithId);

        // The copy if the propertyset with id is equal to the propertyset without id
        assertEquals(propertyset, copyOfPropertysetWithId);
    }

    @Test
    void testCopyValuesOnPropertysetWithIdAndAspect() {
        var valueCreator = new ValueCreatorProvider();
        var id = UUID.randomUUID();
        var propsetId = UUID.randomUUID();
        var propertyset = valueCreator.newPropertyset(propsetId);
        var aspectId = UUID.randomUUID();
        var aspect = valueCreator.newPropertyset(aspectId);
        propertyset.addAspect(aspect);
        populatePropertyset(propertyset, valueCreator, id);

        // Create a Propertyset with the same id and assign the values of the first
        var copy = valueCreator.newPropertyset(propsetId);
        copy.copyValues(propertyset);
        assertNotSame(propertyset, copy); // Obviously...
        // Compare the inner Propertyset since the metadata-recording wrappers compare context in their equals methods
        assertEquals(propertyset, findWrappedPropertyset(copy));

        // Create a Propertyset without id and copy into it and compare
        var propertysetWithoutId = valueCreator.newPropertyset();
        propertysetWithoutId.copyValues(propertyset);
        assertNotSame(propertyset, propertysetWithoutId); // Obviously...
        assertNotEquals(propertyset, propertysetWithoutId); // No "id" on one of them, so not the same
        assertEquals(propertyset.getAspects(), propertysetWithoutId.getAspects());
        assertAllPropertiesExceptIdAndAspectEquals(propertyset, propertysetWithoutId);

        // Test copy to a propertyset with a different id in the same context
        var propertysetWithNewId = valueCreator.newPropertyset(UUID.randomUUID());
        propertysetWithNewId.copyValues(propertyset);
        assertNotEquals(propertyset, propertysetWithNewId); // Different "id" on the two
        assertEquals(propertyset.getAspects(), propertysetWithNewId.getAspects());
        assertAllPropertiesExceptIdAndAspectEquals(propertyset, propertysetWithNewId);
    }

    @Test
    void testAddAspects() {
        var valueCreator = new ValueCreatorProvider();
        var propertyset = valueCreator.newPropertyset(UUID.randomUUID());
        assertFalse(propertyset.hasAspect());
        assertThat(propertyset.getAspects()).isEmpty();

        // Add an aspect to the propertyset and verify that it adds to the list
        var aspect1 = valueCreator.newPropertyset(UUID.randomUUID());
        propertyset.addAspect(aspect1);
        assertTrue(propertyset.hasAspect());
        assertThat(propertyset.getAspects()).hasSize(1);

        // Verify that adding the same aspect again doesn't add to the list
        propertyset.addAspect(aspect1);
        assertThat(propertyset.getAspects()).hasSize(1);

        // Verify that adding a different aspect adds to the list
        var aspect2 = valueCreator.newPropertyset(UUID.randomUUID());
        propertyset.addAspect(aspect2);
        assertThat(propertyset.getAspects()).hasSize(2);
    }

    @Test
    void testThatIdPropertyCantBeOverwritten() {
        var valueCreator = new ValueCreatorProvider();
        var propertyset = valueCreator.newPropertyset(UUID.randomUUID());
        var idValue = propertyset.get(ID_KEY);
        propertyset.setBooleanProperty(ID_KEY, Boolean.TRUE);
        assertEquals(idValue, propertyset.get(ID_KEY));
        propertyset.setBooleanProperty(ID_KEY, true);
        assertEquals(idValue, propertyset.get(ID_KEY));
        propertyset.setLongProperty(ID_KEY, Long.valueOf(42L));
        assertEquals(idValue, propertyset.get(ID_KEY));
        propertyset.setLongProperty(ID_KEY, 42L);
        assertEquals(idValue, propertyset.get(ID_KEY));
        propertyset.setDoubleProperty(ID_KEY, Double.valueOf(3.14));
        assertEquals(idValue, propertyset.get(ID_KEY));
        propertyset.setDoubleProperty(ID_KEY, 3.14);
        assertEquals(idValue, propertyset.get(ID_KEY));
        propertyset.setStringProperty(ID_KEY, "foo");
        assertEquals(idValue, propertyset.get(ID_KEY));
        propertyset.setComplexProperty(ID_KEY, valueCreator.newPropertyset());
        assertEquals(idValue, propertyset.get(ID_KEY));
        propertyset.setReferenceProperty(ID_KEY, valueCreator.newPropertyset());
        assertEquals(idValue, propertyset.get(ID_KEY));
        propertyset.setListProperty(ID_KEY, valueCreator.newValueList());
        assertEquals(idValue, propertyset.get(ID_KEY));
    }

    @Test
    void testMapBehaviour() {
        var valueCreator = new ValueCreatorProvider();
        var mapToCopy = Map.of("pi", valueCreator.fromDouble(3.14), "meaning", valueCreator.fromLong(42L));
        var propertyset = valueCreator.newPropertyset();
        assertThat(propertyset).isEmpty();
        propertyset.putAll(mapToCopy);
        assertThat(propertyset).hasSize(mapToCopy.size());
        assertThat(propertyset.keySet()).containsExactlyInAnyOrderElementsOf(mapToCopy.keySet());
        assertThat(propertyset.entrySet()).hasSize(mapToCopy.entrySet().size());
        assertThat(propertyset.values()).containsExactlyInAnyOrderElementsOf(mapToCopy.values());
        assertTrue(propertyset.containsKey("pi"));
        assertTrue(propertyset.containsValue(new LongValue(42L)));
        var barValue = valueCreator.fromString("bar");
        propertyset.put("foo", barValue);
        assertThat(propertyset).hasSizeGreaterThan(mapToCopy.size());
        assertEquals(barValue, propertyset.remove("foo"));
        assertEquals(valueCreator.getNil(), propertyset.remove("foo"));
        assertThat(propertyset).hasSize(mapToCopy.size());
        propertyset.clear();
        assertThat(propertyset).isEmpty();
    }

    private void assertAllPropertiesExceptIdAndAspectEquals(Propertyset propertyset1, Propertyset propertyset2) {
        var propertynames1 = propertyset1.getPropertynames();
        propertynames1.remove(ID_KEY);
        propertynames1.remove(ASPECTS_KEY);
        var propertynames2 = propertyset2.getPropertynames();
        propertynames2.remove(ID_KEY);
        propertynames2.remove(ASPECTS_KEY);
        assertEquals(propertynames1, propertynames2);
        for (var propertyname : propertynames1) {
            Value value1 = propertyset1.getProperty(propertyname);
            Value value2 = propertyset2.getProperty(propertyname);
            assertEquals(value1, value2);
        }
    }

    private void compareOriginalUnchangedByCopyChange(ValueCreatorProvider valueCreator, Propertyset propertyset, Propertyset copy) {
        assertNotSame(propertyset, copy); // Obviously...
        assertEquals(propertyset, copy);

        // Check that modifying values in the copy doesn't affect the original
        copy.setBooleanProperty("a", false);
        assertFalse(copy.getBooleanProperty("a").booleanValue());
        assertTrue(propertyset.getBooleanProperty("a").booleanValue(), "Expected unchanged value");
        copy.setLongProperty("b", 43);
        assertEquals(43, copy.getLongProperty("b").longValue());
        assertEquals(42, propertyset.getLongProperty("b").longValue(), "Expected unchanged value");
        copy.setDoubleProperty("c", 1.1);
        assertEquals(1.1, copy.getDoubleProperty("c"), 0.0);
        assertEquals(2.7, propertyset.getDoubleProperty("c"), 0.0, "Expected unchanged value");
        copy.setStringProperty("d", "bar foo");
        assertEquals("bar foo", copy.getStringProperty("d"));
        assertEquals("foo bar", propertyset.getStringProperty("d"), "Expected unchanged value");
        Propertyset originalReferencedPropertyset = copy.getReferenceProperty("e");
        Propertyset newReferencedPropertyset = valueCreator.newPropertyset(UUID.randomUUID());
        copy.setReferenceProperty("e", newReferencedPropertyset);
        assertEquals(newReferencedPropertyset, copy.getReferenceProperty("e"));
        assertEquals(originalReferencedPropertyset, propertyset.getReferenceProperty("e"), "Expected unchanged value");
        copy.getComplexProperty("f").setLongProperty("z", 35);
        assertEquals(1, copy.getComplexProperty("f").getPropertynames().size());
        assertEquals(0, propertyset.getComplexProperty("f").getPropertynames().size(), "Expected unchanged value");
        copy.getListProperty("g").add(3.7);
        assertEquals(2, copy.getListProperty("g").size());
        assertEquals(1, propertyset.getListProperty("g").size(), "Expected unchanged value");
    }

    private Value createIdValue(ValueCreatorProvider valueCreator) {
        var propertysetWithId = valueCreator.newPropertyset(UUID.randomUUID());
        return propertysetWithId.getProperty(ID_KEY);
    }

    private Value createAspectsValue(ValueCreatorProvider valueCreator) {
        var aspectlist = valueCreator.newValueList();
        aspectlist.add(valueCreator.newPropertyset(UUID.randomUUID()));
        aspectlist.add(valueCreator.newPropertyset(UUID.randomUUID()));
        aspectlist.add(valueCreator.newPropertyset(UUID.randomUUID()));
        return valueCreator.fromValueList(aspectlist);
    }

    private void populatePropertyset(Propertyset propertyset, ValueCreatorProvider valueCreator, UUID id) {
        propertyset.setBooleanProperty("a", true);
        propertyset.setLongProperty("b", 42);
        propertyset.setDoubleProperty("c", 2.7);
        propertyset.setStringProperty("d", "foo bar");
        propertyset.setReferenceProperty("e", valueCreator.newPropertyset(id));
        propertyset.setComplexProperty("f", valueCreator.newPropertyset());
        var list = newList();
        list.add("foobar");
        propertyset.setListProperty("g", list);
    }

}
