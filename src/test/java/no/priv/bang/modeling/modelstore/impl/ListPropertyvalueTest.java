package no.priv.bang.modeling.modelstore.impl;

import static org.junit.Assert.*;
import no.priv.bang.modeling.modelstore.PropertysetNil;
import no.priv.bang.modeling.modelstore.Propertyvalue;
import no.priv.bang.modeling.modelstore.PropertyvalueList;
import no.priv.bang.modeling.modelstore.PropertyvalueNil;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link ListPropertyvalue}.
 *
 * @author Steinar Bang
 *
 */
public class ListPropertyvalueTest {

    private PropertyvalueArrayList propertyvalueList;
    private Propertyvalue value;

    @Before
    public void setUp() throws Exception {
        propertyvalueList = new PropertyvalueArrayList();
        propertyvalueList.add(new BooleanPropertyvalue(Boolean.TRUE));
        propertyvalueList.add(new LongPropertyvalue(Long.valueOf(42)));
        propertyvalueList.add(new DoublePropertyvalue(Double.valueOf(2.78)));
        propertyvalueList.add(new StringPropertyvalue("foo bar"));
        value = new ListPropertyvalue(propertyvalueList);
    }

    @Test
    public void testIsId() {
    	assertFalse(value.isId());
    }

    @Test
    public void testAsId() {
    	assertEquals(PropertyvalueNil.getNil().asId(), value.asId());
    }

    @Test
    public void testIsBoolean() {
        assertFalse(value.isBoolean());
    }

    @Test
    public void testAsBoolean() {
        assertFalse(value.asBoolean());
    }

    @Test
    public void testIsLong() {
        assertFalse(value.isLong());
    }

    @Test
    public void testAsLong() {
        assertEquals(Long.valueOf(0), value.asLong());
    }

    @Test
    public void testIsDouble() {
        assertFalse(value.isDouble());
    }

    @Test
    public void testAsDouble() {
        assertEquals(Double.valueOf(0.0), value.asDouble());
    }

    @Test
    public void testIsString() {
        assertFalse(value.isString());
    }

    @Test
    public void testAsString() {
        assertEquals("", value.asString());
    }

    @Test
    public void testIsComplexProperty() {
        assertFalse(value.isComplexProperty());
    }

    @Test
    public void testAsComplexProperty() {
        assertEquals(PropertysetNil.getNil(), value.asComplexProperty());
    }

    @Test
    public void testIsReference() {
        assertFalse(value.isReference());
    }

    @Test
    public void testAsReference() {
        assertEquals(PropertysetNil.getNil(), value.asReference());
    }

    @Test
    public void testIsList() {
        assertTrue(value.isList());
    }

    @Test
    public void testAsList() {
    	PropertyvalueList list = value.asList();
    	assertFalse(list.isEmpty());
    	assertEquals(propertyvalueList.size(), list.size());
    }

}