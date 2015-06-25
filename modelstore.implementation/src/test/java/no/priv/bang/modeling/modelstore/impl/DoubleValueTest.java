package no.priv.bang.modeling.modelstore.impl;

import static org.junit.Assert.*;
import static no.priv.bang.modeling.modelstore.impl.Values.*;
import no.priv.bang.modeling.modelstore.Value;
import no.priv.bang.modeling.modelstore.ValueList;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link DoublePropertyvalue}.
 *
 * @author Steinar Bang
 *
 */
public class DoubleValueTest {

    private Value value;

    @Before
    public void setUp() throws Exception {
        value = toDoubleValue(3.14);
    }

    @Test
    public void testIsId() {
    	assertFalse(value.isId());
    }

    @Test
    public void testAsId() {
    	assertEquals(NilValue.getNil().asId(), value.asId());
    }

    @Test
    public void testIsBoolean() {
        assertFalse(value.isBoolean());
    }

    @Test
    public void testAsBoolean() {
        assertTrue(value.asBoolean());
    }

    @Test
    public void testIsLong() {
        assertFalse(value.isLong());
    }

    @Test
    public void testAsLong() {
        assertEquals(Long.valueOf(3), value.asLong());
    }

    @Test
    public void testIsDouble() {
        assertTrue(value.isDouble());
    }

    @Test
    public void testAsDouble() {
        assertEquals(Double.valueOf(3.14), value.asDouble());
    }

    @Test
    public void testIsString() {
        assertFalse(value.isString());
    }

    @Test
    public void testAsString() {
        assertEquals("3.14", value.asString());
    }

    @Test
    public void testIsComplexProperty() {
        assertFalse(value.isComplexProperty());
    }

    @Test
    public void testAsComplexProperty() {
        assertEquals(getNilPropertyset(), value.asComplexProperty());
    }

    @Test
    public void testIsReference() {
        assertFalse(value.isReference());
    }

    @Test
    public void testAsReference() {
        assertEquals(getNilPropertyset(), value.asReference());
    }

    @Test
    public void testIsList() {
        assertFalse(value.isList());
    }

    @Test
    public void testAsList() {
    	ValueList emptyList = value.asList();
    	assertTrue(emptyList.isEmpty());
    }

    /**
     * Test av {@link DoublePropertyvalue#hashCode()}.
     */
    @Test
    public void testHashCode() {
        Value nullDoubleValue = toDoubleValue(null);
        assertEquals(31, nullDoubleValue.hashCode());
        Value pi = toDoubleValue(3.14);
        assertEquals(300063686, pi.hashCode());
    }

    /**
     * Test av {@link DoublePropertyvalue#equals(Object)}.
     */
    @Test
    public void testEquals() {
        Value nullDoubleValue = toDoubleValue(null);
        assertFalse(nullDoubleValue.equals(null));
        assertFalse(nullDoubleValue.equals(NilValue.getNil()));
        assertTrue(nullDoubleValue.equals(nullDoubleValue));
        assertFalse(nullDoubleValue.equals(value));
        assertFalse(value.equals(nullDoubleValue));
    }

    /**
     * Test av {@link DoublePropertyvalue#toString()}.
     */
    @Test
    public void testToString() {
        Value nullDoubleValue = toDoubleValue(null);
        assertEquals("DoublePropertyvalue [value=0.0]", nullDoubleValue.toString());
        Value e = toDoubleValue(2.78);
        assertEquals("DoublePropertyvalue [value=2.78]", e.toString());
    }

}
