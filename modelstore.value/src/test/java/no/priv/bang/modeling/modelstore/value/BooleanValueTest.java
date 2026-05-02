package no.priv.bang.modeling.modelstore.value;

import static no.priv.bang.modeling.modelstore.value.Values.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.priv.bang.modeling.modelstore.services.Value;

/**
 * Unit tests for {@link BooleanValue}.
 *
 */
class BooleanValueTest {

    private Value value;

    @BeforeEach
    void setUp() throws Exception {
        value = toBooleanValue(Boolean.TRUE);
    }

    @Test
    void testIsId() {
        assertFalse(value.isId());
    }

    @Test
    void testAsId() {
        assertEquals(getNil().asId(), value.asId());
    }

    @Test
    void testIsBoolean() {
        assertTrue(value.isBoolean());
    }

    @Test
    void testAsBoolean() {
        assertTrue(value.asBoolean());
    }

    @Test
    void testIsLong() {
        assertFalse(value.isLong());
    }

    @Test
    void testAsLong() {
        assertEquals(Long.valueOf(1), value.asLong());
    }

    @Test
    void testIsDouble() {
        assertFalse(value.isDouble());
    }

    @Test
    void testAsDouble() {
        assertEquals(Double.valueOf(1), value.asDouble());
    }

    @Test
    void testIsString() {
        assertFalse(value.isString());
    }

    @Test
    void testAsString() {
        assertEquals("true", value.asString());
    }

    @Test
    void testIsComplexProperty() {
        assertFalse(value.isComplexProperty());
    }

    @Test
    void testAsComplexProperty() {
        assertEquals(getNilPropertyset(), value.asComplexProperty());
    }

    @Test
    void testIsReference() {
        assertFalse(value.isReference());
    }

    @Test
    void testAsReference() {
        assertEquals(getNilPropertyset(), value.asReference());
    }

    @Test
    void testIsList() {
        assertFalse(value.isList());
    }

    @Test
    void testAsList() {
        var emptyList = value.asList();
        assertTrue(emptyList.isEmpty());
    }

    /**
     * Test av {@link BooleanValue#hashCode()}.
     */
    @Test
    void testHashCode() {
        var nullBooleanValue = toBooleanValue(null);
        assertEquals(1268, nullBooleanValue.hashCode());
        var trueBooleanValue = toBooleanValue(Boolean.TRUE);
        assertEquals(1262, trueBooleanValue.hashCode());
    }

    /**
     * Test av {@link BooleanValue#equals(Object)}.
     */
    @Test
    void testEquals() {
        var nullBooleanValue = toBooleanValue(null);
        var falseBooleanValue = toBooleanValue(Boolean.FALSE);
        var trueBooleanValue = toBooleanValue(Boolean.TRUE);
        assertNotEquals(nullBooleanValue, null); // NOSONAR the point here is to test propertyset.equals, so no the arguments should not be swapped
        assertNotEquals(nullBooleanValue, getNil());
        assertEquals(nullBooleanValue, nullBooleanValue);
        assertEquals(nullBooleanValue, falseBooleanValue);
        assertNotEquals(nullBooleanValue, trueBooleanValue);
    }

    /**
     * Test av {@link BooleanValue#toString()}.
     */
    @Test
    void testToString() {
        var nullBooleanValue = toBooleanValue(null);
        assertEquals("BooleanValue [value=false]", nullBooleanValue.toString());
        var falseBooleanValue = toBooleanValue(Boolean.FALSE);
        assertEquals("BooleanValue [value=false]", falseBooleanValue.toString());
        var trueBooleanValue = toBooleanValue(Boolean.TRUE);
        assertEquals("BooleanValue [value=true]", trueBooleanValue.toString());
    }
}
