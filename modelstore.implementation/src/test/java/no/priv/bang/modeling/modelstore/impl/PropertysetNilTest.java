package no.priv.bang.modeling.modelstore;

import static org.junit.Assert.*;
import static no.priv.bang.modeling.modelstore.impl.Values.*;
import no.priv.bang.modeling.modelstore.impl.PropertysetImpl;

import org.junit.Test;

/**
 * Unit tests for {@link PropertysetNil}.
 *
 * @author Steinar Bang
 *
 */
public class PropertysetNilTest {

    /**
     * Unit test for {@link PropertysetNil#getPropertynames()}.
     */
    @Test
    public void testGetPropertyNames() {
        Propertyset nilPropertyset = PropertysetNil.getNil();
        assertEquals(0, nilPropertyset.getPropertynames().size());
    }

    /**
     * Unit test for {@link PropertysetNil#getProperty()} and
     * {@link PropertysetNil#setProperty(String, Propertyvalue)}
     */
    @Test
    public void testGetSetGetProperty() {
        Propertyset nilPropertyset = PropertysetNil.getNil();
        Propertyset propertyset = new PropertysetImpl();
        propertyset.setStringProperty("string", "this is stringvalue");
        assertEquals(getNil(), nilPropertyset.getProperty("nomatter"));

        // Verify that properties can't be set.
        nilPropertyset.setProperty("nomatter", toStringValue("foo bar"));
        // Reading back, the value is still nil
        assertEquals(getNil(), nilPropertyset.getProperty("nomatter"));

        // Boolean properties can't be set
        assertFalse(nilPropertyset.getBooleanProperty("boolean"));
        nilPropertyset.setBooleanProperty("boolean", true);
        assertFalse(nilPropertyset.getBooleanProperty("boolean"));

        // Double properties can't be set
        assertEquals(0.0, nilPropertyset.getDoubleProperty("double").doubleValue(), 0);
        nilPropertyset.setDoubleProperty("double", 3.14);
        assertEquals(0.0, nilPropertyset.getDoubleProperty("double").doubleValue(), 0);

        // Long properties can't be set
        assertEquals(0L, nilPropertyset.getLongProperty("long").longValue(), 0);
        nilPropertyset.setLongProperty("long", 42L);
        assertEquals(0L, nilPropertyset.getLongProperty("long").longValue(), 0);

        // String properties can't be set
        assertEquals("", nilPropertyset.getStringProperty("string"));
        nilPropertyset.setStringProperty("string", "foo bar");
        assertEquals("", nilPropertyset.getStringProperty("string"));

        // Complex properties can't be set
        assertEquals(nilPropertyset, nilPropertyset.getComplexProperty("complex"));
        nilPropertyset.setComplexProperty("complex", propertyset);
        assertEquals(nilPropertyset, nilPropertyset.getComplexProperty("complex"));

        // Reference properties can't be set
        assertEquals(nilPropertyset, nilPropertyset.getReferenceProperty("reference"));
        nilPropertyset.setReferenceProperty("reference", propertyset);
        assertEquals(nilPropertyset, nilPropertyset.getReferenceProperty("reference"));

        // List properties can't be set
        assertEquals(0, nilPropertyset.getListProperty("list").size());
        ValueList list = newList();
        list.add(toStringValue("bar"));
        nilPropertyset.setListProperty("list", list);
        assertEquals(0, nilPropertyset.getListProperty("list").size());
    }

    /**
     * Unit test for {@link PropertysetNil#hasAspect()} and
     * {@link PropertysetNil#getAspects()} and
     * {@link PropertysetNil#addAspect(Propertyset)}.
     */
    @Test
    public void testAspects() {
        Propertyset nilPropertyset = PropertysetNil.getNil();
        Propertyset propertyset = new PropertysetImpl();
        propertyset.setStringProperty("string", "this is stringvalue");

        assertFalse(nilPropertyset.hasAspect());

        // The list of aspects is empty.
        assertEquals(0, nilPropertyset.getAspects().size());

        // Verify that no aspects can be added
        nilPropertyset.addAspect(propertyset);
        // Reading back, the value is still empty.
        assertEquals(0, nilPropertyset.getAspects().size());
    }

    /**
     * Unit test for {@link PropertysetNil#equals()}.
     */
    @Test
    public void testEquals() {
        Propertyset nilPropertyset = PropertysetNil.getNil();
        Propertyset propertyset = new PropertysetImpl();

        assertTrue(nilPropertyset.equals(nilPropertyset));
        assertFalse(nilPropertyset.equals(null));

        // Nil compares equals to an empty non-nil propertyset
        assertTrue(nilPropertyset.equals(propertyset));

        // But add a property to that propertyset and it will no longer compare
        propertyset.setStringProperty("string", "this is stringvalue");
        assertFalse(nilPropertyset.equals(propertyset));

        // A string is not equals to a propertyset, nil or not.
        assertFalse(nilPropertyset.equals("foo bar"));
    }
}
