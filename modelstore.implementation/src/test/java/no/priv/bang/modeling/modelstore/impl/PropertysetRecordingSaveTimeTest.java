package no.priv.bang.modeling.modelstore.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.UUID;

import no.priv.bang.modeling.modelstore.Propertyset;
import no.priv.bang.modeling.modelstore.PropertysetContext;
import no.priv.bang.modeling.modelstore.ValueList;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link PropertysetRecordingSaveTime}.
 *
 * @author Steinar Bang
 *
 */
public class PropertysetRecordingSaveTimeTest {

    private final static UUID generalObjectId = UUID.fromString("06cee83c-2ca8-44b8-8035-c79586665532");
    private final static UUID propertysetId = UUID.fromString("a72f6189-f132-4714-8f11-6258967a74ce");
    private PropertysetContext innerContext;
    private PropertysetContext context;
    private Propertyset propertyset;
    @Before
    public void setup() {
        innerContext = new PropertysetContextImpl();
        context = new PropertysetContextRecordingMetadata(innerContext);
        propertyset = context.findPropertyset(propertysetId );
        addProperties(propertyset);
    }

    private void addProperties(Propertyset propertyset2) {
        propertyset.addAspect(context.findPropertyset(generalObjectId));
        propertyset.setBooleanProperty("a", true);
        propertyset.setLongProperty("b", 1);
        propertyset.setDoubleProperty("c", 1.1);
        propertyset.setStringProperty("d", "foo bar");
        propertyset.setComplexProperty("e", context.createPropertyset());
        propertyset.getComplexProperty("e").setBooleanProperty("aa", true);
        propertyset.getComplexProperty("e").setLongProperty("bb", 2);
        Propertyset other = context.findPropertyset(UUID.fromString("72c5cd3a-178e-4579-ace5-72d857a0d953"));
        other.addAspect(context.findPropertyset(generalObjectId));
        other.setStringProperty("cc", "bar foo");
        other.setDoubleProperty("dd", 2.1);
        propertyset.setReferenceProperty("f", other);
        propertyset.setListProperty("g", context.createList());
        propertyset.getListProperty("g").add(1);
        propertyset.getListProperty("g").add(2);
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#isNil()}.
     */
    @Test
    public void testIsNil() {
        assertFalse(propertyset.isNil());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#getPropertynames()}.
     */
    @Test
    public void testGetPropertynames() {
        assertEquals(9, propertyset.getPropertynames().size());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#getProperty()}.
     */
    @Test
    public void testGetProperty() {
        assertTrue(propertyset.getProperty("a").isBoolean());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setProperty()}.
     * @throws InterruptedException
     */
    @Test
    public void testSetProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = context.getLastmodifieddate(propertyset);
        Thread.sleep(10); // Sleep a little to get a different timestamp
        propertyset.setProperty("a", Values.toDoubleValue(1.7));
        Date lastmodifiedTimeAfterSetProperty = context.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty);
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#hasAspect()}.
     */
    @Test
    public void testHasAspect() {
        assertTrue(propertyset.hasAspect());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#getAspects()}.
     */
    @Test
    public void testGetAspects() {
        assertEquals(1, propertyset.getAspects().size());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#hasId()}.
     */
    @Test
    public void testHasId() {
        assertTrue(propertyset.hasId());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#getId()}.
     */
    @Test
    public void testGetId() {
        assertEquals(propertysetId, propertyset.getId());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setBooleanProperty(String, boolean)},
     * {@link PropertysetRecordingSaveTime#setBooleanProperty(String, Boolean)}, and
     * {@link PropertysetRecordingSaveTime#getBooleanProperty(String)}
     * @throws InterruptedException
     */
    @Test
    public void testSetGetBooleanProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = context.getLastmodifieddate(propertyset);
        Thread.sleep(10); // Sleep a little to get a different timestamp
        propertyset.setBooleanProperty("a", Boolean.FALSE);
        Date lastmodifiedTimeAfterSetProperty1 = context.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty1);
        assertFalse(propertyset.getBooleanProperty("a"));
        Thread.sleep(10); // Sleep a little to get a different timestamp
        propertyset.setBooleanProperty("a", true);
        Date lastmodifiedTimeAfterSetProperty2 = context.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeAfterSetProperty1, lastmodifiedTimeAfterSetProperty2);
        assertTrue(propertyset.getBooleanProperty("a"));
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setLongProperty(String, long)},
     * {@link PropertysetRecordingSaveTime#setLongProperty(String, Long)}, and
     * {@link PropertysetRecordingSaveTime#getLongProperty(String)}
     * @throws InterruptedException
     */
    @Test
    public void testSetGetLongProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = context.getLastmodifieddate(propertyset);
        Thread.sleep(10); // Sleep a little to get a different timestamp
        propertyset.setLongProperty("b", Long.valueOf(128));
        Date lastmodifiedTimeAfterSetProperty1 = context.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty1);
        assertEquals(128, propertyset.getLongProperty("b").longValue());
        Thread.sleep(10); // Sleep a little to get a different timestamp
        propertyset.setLongProperty("b", 127);
        Date lastmodifiedTimeAfterSetProperty2 = context.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeAfterSetProperty1, lastmodifiedTimeAfterSetProperty2);
        assertEquals(127, propertyset.getLongProperty("b").longValue());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setDoubleProperty(String, double)},
     * {@link PropertysetRecordingSaveTime#setDoubleProperty(String, Double)}, and
     * {@link PropertysetRecordingSaveTime#getDoubleProperty(String)}
     * @throws InterruptedException
     */
    @Test
    public void testSetGetDoubleProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = context.getLastmodifieddate(propertyset);
        Thread.sleep(10); // Sleep a little to get a different timestamp
        propertyset.setDoubleProperty("c", Double.valueOf(12.8));
        Date lastmodifiedTimeAfterSetProperty1 = context.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty1);
        assertEquals(12.8, propertyset.getDoubleProperty("c").doubleValue(), 0.0);
        Thread.sleep(10); // Sleep a little to get a different timestamp
        propertyset.setDoubleProperty("c", 1.27);
        Date lastmodifiedTimeAfterSetProperty2 = context.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeAfterSetProperty1, lastmodifiedTimeAfterSetProperty2);
        assertEquals(1.27, propertyset.getDoubleProperty("c").doubleValue(), 0.0);
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setStringProperty(String, String)},
     * and {@link PropertysetRecordingSaveTime#getStringProperty(String)}
     * @throws InterruptedException
     */
    @Test
    public void testSetGetStringProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = context.getLastmodifieddate(propertyset);
        Thread.sleep(10); // Sleep a little to get a different timestamp
        propertyset.setStringProperty("d", "abcd");
        Date lastmodifiedTimeAfterSetProperty = context.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty);
        assertEquals("abcd", propertyset.getStringProperty("d"));
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setComplexProperty(String, Propertyset)},
     * and {@link PropertysetRecordingSaveTime#getComplexProperty(String)}
     * @throws InterruptedException
     */
    @Test
    public void testSetGetComplexProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = context.getLastmodifieddate(propertyset);
        Thread.sleep(10); // Sleep a little to make it possible to get a different time stamp
        // This is a back door: it is possible to manipulate a complex property without changing the timestamp on the propertyset
        propertyset.getComplexProperty("e").setStringProperty("cc", "modified");
        Date lastmodifiedTimeAfterSetProperty1 = context.getLastmodifieddate(propertyset);
        assertEquals("Expected the time stamps to be identical", lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty1);
        assertEquals("modified", propertyset.getComplexProperty("e").getStringProperty("cc"));
        Thread.sleep(10); // Sleep a little to get a different timestamp
        Propertyset complex = propertyset.getComplexProperty("e");
        complex.setStringProperty("cc", "modified again");
        propertyset.setComplexProperty("e", complex);
        Date lastmodifiedTimeAfterSetProperty2 = context.getLastmodifieddate(propertyset);
        assertNotEquals("Expected modification time to be changed", lastmodifiedTimeAfterSetProperty1, lastmodifiedTimeAfterSetProperty2);
        assertEquals("modified again", propertyset.getComplexProperty("e").getStringProperty("cc"));
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setReferenceProperty(String, Propertyset)},
     * and {@link PropertysetRecordingSaveTime#getReferenceProperty(String)}
     * @throws InterruptedException
     */
    @Test
    public void testSetGetReferenceProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = context.getLastmodifieddate(propertyset);
        Thread.sleep(10); // Sleep a little to get a different timestamp
        UUID newReferencedPropertysetId = UUID.randomUUID();
        propertyset.setReferenceProperty("f", context.findPropertyset(newReferencedPropertysetId));
        Date lastmodifiedTimeAfterSetProperty = context.getLastmodifieddate(propertyset);
        assertNotEquals(lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty);
        assertEquals(newReferencedPropertysetId, propertyset.getReferenceProperty("f").getId());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#setListProperty(String, Propertyset)},
     * and {@link PropertysetRecordingSaveTime#getListProperty(String)}
     * @throws InterruptedException
     */
    @Test
    public void testSetGetListProperty() throws InterruptedException {
        // Expected the set value to change the lastmodifiedtime of the propertyset
        Date lastmodifiedTimeBeforeSetProperty = context.getLastmodifieddate(propertyset);
        Thread.sleep(10); // Sleep a little to make it possible to get a different time stamp
        // This is a back door: it is possible to manipulate a list property without changing the timestamp on the propertyset
        propertyset.getListProperty("g").add("modified");
        Date lastmodifiedTimeAfterSetProperty1 = context.getLastmodifieddate(propertyset);
        assertEquals("Expected the time stamps to be identical", lastmodifiedTimeBeforeSetProperty, lastmodifiedTimeAfterSetProperty1);
        assertEquals(3, propertyset.getListProperty("g").size());
        Thread.sleep(10); // Sleep a little to get a different timestamp
        ValueList list = propertyset.getListProperty("g");
        list.add("modified again");
        propertyset.setListProperty("g", list);
        Date lastmodifiedTimeAfterSetProperty2 = context.getLastmodifieddate(propertyset);
        assertNotEquals("Expected modification time to be changed", lastmodifiedTimeAfterSetProperty1, lastmodifiedTimeAfterSetProperty2);
        assertEquals(4, propertyset.getListProperty("g").size());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#hashCode()}.
     */
    @Test
    public void testHashCode() {
        assertEquals(-1095503838, propertyset.hashCode());
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#equals(Object)}.
     */
    @Test
    public void testEquals() {
        assertTrue(propertyset.equals(propertyset));
        assertFalse(propertyset.equals(null));
        // Same underlying object, different wrapper
        Propertyset copyOfPropertyset = context.findPropertyset(propertysetId);
        assertTrue(propertyset.equals(copyOfPropertyset));
        assertTrue(copyOfPropertyset.equals(propertyset));

        // Same underlying object, different context
        PropertysetContext context2 = new PropertysetContextRecordingMetadata(innerContext);
        Propertyset otherContextPropertyset = context2.findPropertyset(propertysetId);
        assertFalse(propertyset.equals(otherContextPropertyset));

        // Same context, different propertyset
        Propertyset otherPropertyset = context.findPropertyset(UUID.randomUUID());
        assertFalse(propertyset.equals(otherPropertyset));

        // Compare underlying object
        Propertyset inner = innerContext.findPropertyset(propertysetId);
        assertTrue("Expected inner object to compare equals to wrapper", propertyset.equals(inner));
    }

    /**
     * Unit test of {@link PropertysetRecordingSaveTime#toString()}.
     */
    @Test
    public void testToString() {
        assertThat(propertyset.toString(), startsWith("PropertysetRecordingSaveTime "));
    }
}
