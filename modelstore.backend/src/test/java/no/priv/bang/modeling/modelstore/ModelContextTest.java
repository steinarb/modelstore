package no.priv.bang.modeling.modelstore;


import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.UUID;

import static no.priv.bang.modeling.modelstore.impl.Values.*;
import static no.priv.bang.modeling.modelstore.impl.Propertysets.*;
import static no.priv.bang.modeling.modelstore.impl.Aspects.*;
import no.priv.bang.modeling.modelstore.impl.JsonGeneratorWithReferences;
import no.priv.bang.modeling.modelstore.impl.JsonPropertysetPersister;
import no.priv.bang.modeling.modelstore.impl.ModelstoreProvider;
import static no.priv.bang.modeling.modelstore.testutils.TestUtils.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Unit test for the {@link ModelContext} interface and its
 * implementations.
 *
 * @author Steinar Bang
 *
 */
public class ModelContextTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testCreatePropertyset() {
        Modelstore modelstore = new ModelstoreProvider().get();
        ModelContext context = modelstore.getDefaultContext();

        // Get a propertyset instance and verify that it is a non-nil instance
        // that can be modified.
        Propertyset propertyset = context.createPropertyset();
        assertFalse(propertyset.isNil());
        assertFalse(propertyset.hasId());
        assertEquals(getNil().asId(), propertyset.getId());
        // First get the default value for a non-existing property
        assertEquals("", propertyset.getStringProperty("stringProperty"));
        // Set the value as a different type
        propertyset.setDoubleProperty("stringProperty", Double.valueOf(3.14));
        // Verify that the property now contains a non-default value
        assertEquals("3.14", propertyset.getStringProperty("stringProperty"));
    }

    @Test
    public void testList() {
        Modelstore modelstore = new ModelstoreProvider().get();
        ModelContext context = modelstore.getDefaultContext();

        ValueList list = context.createList();
        assertEquals(0, list.size());
        list.add(true);
        assertTrue(list.get(0).asBoolean());
        list.add(3.14);
        assertEquals(3.14, list.get(1).asDouble(), 0.0);

        Propertyset referencedPropertyset = context.findPropertyset(UUID.randomUUID());
        list.add(referencedPropertyset);
        assertTrue(list.get(2).isReference());

        Propertyset containedPropertyset = context.createPropertyset();
        list.add(containedPropertyset);
        assertTrue(list.get(3).isComplexProperty());

        list.set(0, 42); // Overwrite boolean with long
        assertTrue(list.get(0).asBoolean()); // Non-null value gives true boolean
        assertEquals(42, list.get(0).asLong().longValue()); // The actual long value is still there

        ValueList containedlist = context.createList();
        containedlist.add(100);
        list.add(containedlist);
        assertEquals(1, list.get(4).asList().size());
        // Modifying original does not affect list element
        containedlist.add(2.5);
        assertEquals(2, containedlist.size());
        assertEquals(1, list.get(4).asList().size());

        assertEquals(5, list.size());
        list.remove(1);
        assertEquals(4, list.size());
    }

    @Test
    public void testEmbeddedAspects() {
        Modelstore modelstore = new ModelstoreProvider().get();
        ModelContext context = modelstore.getDefaultContext();
        int numberOfEmbeddedAspects = 6; // Adjust when adding embedded aspects

        Collection<Propertyset> aspects = context.listAllAspects();
        assertEquals(numberOfEmbeddedAspects, aspects.size());
    }

    @Test
    public void testFindPropertysetById() {
        Modelstore modelstore = new ModelstoreProvider().get();
        ModelContext context = modelstore.getDefaultContext();

        // Get a propertyset by id and verify that it is empty initially
        UUID newPropertysetId = UUID.randomUUID();
        Propertyset propertyset = context.findPropertyset(newPropertysetId);
        assertTrue(propertyset.hasId());
        assertEquals(newPropertysetId, propertyset.getId());

        // Check that property values can be set and retrieved
        assertEquals("", propertyset.getStringProperty("stringProperty"));
        propertyset.setStringProperty("stringProperty", "foo bar");
        assertEquals("foo bar", propertyset.getStringProperty("stringProperty"));

        // Check that the "id" property can't be set
        propertyset.setStringProperty("id", "foo bar");
        assertEquals("Expected the \"id\" property to still hold the id", newPropertysetId.toString(), propertyset.getStringProperty("id"));
        propertyset.setBooleanProperty("id", Boolean.TRUE);
        assertEquals("Expected the \"id\" property to not be affected by setting a boolean value", Boolean.FALSE, propertyset.getBooleanProperty("id"));
        propertyset.setLongProperty("id", Long.valueOf(7));
        assertEquals("Expected the \"id\" property to not be affected by setting an integer value", Long.valueOf(0), propertyset.getLongProperty("id"));
        propertyset.setDoubleProperty("id", Double.valueOf(3.14));
        assertEquals("Expected the \"id\" property to not be affected by setting an integer value", Double.valueOf(0.0), propertyset.getDoubleProperty("id"));
        Propertyset complexValue = context.createPropertyset();
        propertyset.setComplexProperty("id", complexValue);
        Propertyset returnedComplexProperty = propertyset.getComplexProperty("id");
        assertEquals("Expected the \"id\" property not to be affected by setting a complex value", getNilPropertyset(), returnedComplexProperty);
        assertFalse(returnedComplexProperty.hasId());
        assertEquals(getNil().asId(), returnedComplexProperty.getId());
        Propertyset referencedPropertyset = context.findPropertyset(UUID.randomUUID());
        referencedPropertyset.setReferenceProperty("id", referencedPropertyset);
        assertEquals("Expected the \"id\" property not to be affected by setting an object reference", getNilPropertyset(), propertyset.getReferenceProperty("id"));
        ValueList listValue = newList();
        propertyset.setListProperty("id", listValue);
        assertEquals("Expected the \"id\" property not to be affected by setting an object reference", getNil().asList(), propertyset.getListProperty("id"));

        // Verify that asking for the same id again will return the same object
        assertEquals(propertyset, context.findPropertyset(newPropertysetId));
    }

    @Test
    public void testFindPropertysetOfAspect() {
        Modelstore modelstore = new ModelstoreProvider().get();
        ModelContext context = modelstore.getDefaultContext();

        buildModelWithAspects(context);

        // Get all aspects currently in the context
        Collection<Propertyset> aspects = context.listAllAspects();
        assertEquals(9, aspects.size());

        Propertyset vehicle = findAspectByTitle(aspects, "vehicle");
        Collection<Propertyset> vehicles = context.findObjectsOfAspect(vehicle);
        assertEquals(5, vehicles.size());

        Propertyset car = findAspectByTitle(aspects, "car");
        Collection<Propertyset> cars = context.findObjectsOfAspect(car);
        assertEquals(3, cars.size());
    }

    /**
     * Test setting multiple aspects on a Propertyset
     */
    @Test
    public void testPropertysetWithMultipleAspects() {
        Modelstore modelstore = new ModelstoreProvider().get();
        ModelContext context = modelstore.getDefaultContext();

        // Create two aspects
        Propertyset generalObjectAspect = buildGeneralObjectAspect(context);
        Propertyset positionAspect = buildPositionAspect(context);

        // Get a brand new aspectless Propertyset
        Propertyset propertyset = context.findPropertyset(UUID.randomUUID());

        // Verify that the propertyset has no aspects
        assertEquals(0, propertyset.getAspects().size());

        // Add an aspect and verify that the propertyset now has an aspect
        propertyset.addAspect(generalObjectAspect);
        assertEquals(1, propertyset.getAspects().size());

        // Add a new aspect and verify that the propertyset now has two aspects
        propertyset.addAspect(positionAspect);
        assertEquals(2, propertyset.getAspects().size());

        // Add an already existing aspect again and observe that the propertyset
        // still only has two aspects.
        propertyset.addAspect(generalObjectAspect);
        assertEquals(2, propertyset.getAspects().size());
    }

    @Test
    public void experimentalJacksonPersist() throws IOException {
        Modelstore modelstore = new ModelstoreProvider().get();
        ModelContext context = modelstore.getDefaultContext();
        buildModelWithAspects(context);

        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);
        File propertysetsFile = folder.newFile("propertysets.json");
        persister.persist(propertysetsFile, context);

        // Parse the written data
        Modelstore modelstore2 = new ModelstoreProvider();
        ModelContext context2 = modelstore2.getDefaultContext();
        persister.restore(propertysetsFile, context2);

        // verify that what's parsed is what went in.
        assertEquals(context.listAllPropertysets().size(), context2.listAllPropertysets().size());
        compareAllPropertysets(context, context2);
    }

    @Test
    public void testJsonGeneratorWithReference() throws IOException {
        // Create two propertysets with ids, and make a reference to propertyset
        // "b" from propertyset "a".
        Modelstore modelstore = new ModelstoreProvider().get();
        ModelContext context = modelstore.getDefaultContext();
        UUID idA = UUID.randomUUID();
        Propertyset a = context.findPropertyset(idA);
        UUID idB = UUID.randomUUID();
        Propertyset b = context.findPropertyset(idB);
        a.setReferenceProperty("b", b);

        // Create a factory
        JsonFactory jsonFactory = new JsonFactory();

        // Write an objectId
        File objectIdFile = folder.newFile("objectid.json");
        JsonGenerator generator = new JsonGeneratorWithReferences(jsonFactory.createGenerator(objectIdFile, JsonEncoding.UTF8));
        assertTrue(generator.canWriteObjectId());
        generator.writeObjectId(a.getId());
        generator.close();

        // Check that the written objectId looks like expected (a JSON quoted string)
        String expectedObjectIdAsJson = "\"" + idA.toString() + "\"";
        String objectId = new String(Files.readAllBytes(objectIdFile.toPath()));
        assertEquals(expectedObjectIdAsJson, objectId);

        // Write an object reference
        File objectReferenceFile = folder.newFile("objectreference.json");
        JsonGenerator generator2 = new JsonGeneratorWithReferences(jsonFactory.createGenerator(objectReferenceFile, JsonEncoding.UTF8));
        assertTrue(generator2.canWriteObjectId());
        generator2.writeObjectRef(a.getReferenceProperty("b").getId());
        generator2.close();

        // Check that the written objectReference looks like the expected JSON
        String expectedObjectReferenceAsJson = "{\"ref\":\"" + idB.toString() + "\"}";
        String objectReference = new String(Files.readAllBytes(objectReferenceFile.toPath()));
        assertEquals(expectedObjectReferenceAsJson, objectReference);
    }

    /**
     * Unit test for {@link ModelContext#merge(ModelContext} when the
     * two modelcontexts being merged has no overlap.
     * @throws IOException
     */
    @Test
    public void testMergeNoOverlapBetweenContexts() throws IOException {
        Modelstore modelstore = new ModelstoreProvider().get();
        ModelContext context = modelstore.createContext();
        buildPropertysetA(context, UUID.randomUUID());
        assertEquals("Expected context to contain metadata+1 propertyset", 2, context.listAllPropertysets().size());

        ModelContext otherContext = modelstore.createContext();
        UUID bId = UUID.randomUUID();
        buildPropertysetB(otherContext, bId);
        assertEquals("Expected otherContext to contain metadata+1 propertyset", 2, otherContext.listAllPropertysets().size());

        context.merge(otherContext);
        assertEquals("Expected context to contain metadata+2 propertysets", 3, context.listAllPropertysets().size());
        // Verify that the copied "B" is the same as the original B
        // TODO decide if PropertysetRecordingSaveTime.equals() should include the context in comparison, for now: get the inner PropertysetImpl instances and compare them instead
        Propertyset originalB = findWrappedPropertyset(otherContext.findPropertyset(bId));
        Propertyset mergedB = findWrappedPropertyset(context.findPropertyset(bId));
        assertEquals(originalB, mergedB);

        // Save and restore the merged context and verify that the restored context is the same as the merged context
        File propertysetsFile = folder.newFile("mergedcontext.json");
        OutputStream saveStream = Files.newOutputStream(propertysetsFile.toPath());
        modelstore.persistContext(saveStream, context);
        InputStream loadStream = Files.newInputStream(propertysetsFile.toPath());
        ModelContext restoredContext = modelstore.restoreContext(loadStream);
        compareAllPropertysets(context, restoredContext);
    }

    /**
     * Unit test for {@link ModelContext#merge(ModelContext} when the
     * two modelcontexts being merged has overlap: the and b objects exists
     * in both contexts and the newest values are kept. a is newest in the
     * otherContext and b is newest in context.
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testMergeWithOverlapBetweenContexts() throws IOException, InterruptedException {
        Modelstore modelstore = new ModelstoreProvider().get();
        ModelContext context = modelstore.createContext();
        UUID aId = UUID.randomUUID();
        buildPropertysetA(context, aId);
        assertEquals("Expected context to contain metadata+1 propertyset", 2, context.listAllPropertysets().size());

        // Wait a few milliseconds to get a different time stamp
        Thread.sleep(10);
        ModelContext otherContext = modelstore.createContext();
        UUID bId = UUID.randomUUID();
        buildPropertysetA(otherContext, aId);
        otherContext.findPropertyset(aId).setLongProperty("value", 42);
        Propertyset generalObjectAspect = otherContext.findPropertyset(generalObjectAspectId);
        otherContext.findPropertyset(aId).addAspect(generalObjectAspect);
        buildPropertysetB(otherContext, bId);
        otherContext.findPropertyset(bId).addAspect(generalObjectAspect);
        assertEquals("Expected otherContext to contain metadata+2 propertysets", 3, otherContext.listAllPropertysets().size());

        // Wait a few milliseconds to get a different time stamp, then create "b" in the
        // in the first context, with a slightly newer time stamp, meaning it should be kept
        Thread.sleep(10);
        buildPropertysetB(context, bId);
        context.findPropertyset(bId).setLongProperty("value", 4); // Change the value, should be kept after merge
        Propertyset modelAspect = context.findPropertyset(modelAspectId);
        context.findPropertyset(bId).addAspect(modelAspect);
        assertEquals("Expected context to contain metadata+2 propertysets", 3, context.listAllPropertysets().size());

        context.merge(otherContext);

        // Verify the merge results
        assertEquals("Expected context to contain metadata+2 propertysets", 3, context.listAllPropertysets().size());
        // Check that the "value" in "b" is from "context" and the "value" in "a" is from "otherContext"
        assertEquals(42, context.findPropertyset(aId).getLongProperty("value").longValue());
        assertEquals(4, context.findPropertyset(bId).getLongProperty("value").longValue());
        // Check that "a" has aspect "general object"
        assertEquals(1, context.findPropertyset(aId).getAspects().size());
        assertEquals(generalObjectAspectId, context.findPropertyset(aId).getAspects().get(0).asReference().getId());
        // Check that "b" has two aspects: first "model" (oldest) and then "general object" (newest)
        assertEquals(2, context.findPropertyset(bId).getAspects().size());
        assertEquals(modelAspectId, context.findPropertyset(bId).getAspects().get(0).asReference().getId());
        assertEquals(generalObjectAspectId, context.findPropertyset(bId).getAspects().get(1).asReference().getId());

        // Save and restore the merged context and verify that the restored context is the same as the merged context
        File propertysetsFile = folder.newFile("mergedcontext.json");
        OutputStream saveStream = Files.newOutputStream(propertysetsFile.toPath());
        modelstore.persistContext(saveStream, context);
        InputStream loadStream = Files.newInputStream(propertysetsFile.toPath());
        ModelContext restoredContext = modelstore.restoreContext(loadStream);
        compareAllPropertysets(context, restoredContext);
    }

    /**
     * Corner case unit test for {@link ModelContext#merge(ModelContext}.
     * Test what happens when merging with a null.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testMergeWithNull() throws IOException, InterruptedException {
        Modelstore modelstore = new ModelstoreProvider().get();
        ModelContext context = modelstore.createContext();
        UUID aId = UUID.randomUUID();
        buildPropertysetA(context, aId);
        assertEquals("Expected context to contain metadata+1 propertyset", 2, context.listAllPropertysets().size());
        Collection<Propertyset> propertysetsBeforeMerge = context.listAllPropertysets();

        // Try merging with null
        context.merge(null);

        // Verify that the contents are the same as before merging with null
        assertEquals(propertysetsBeforeMerge, context.listAllPropertysets());
    }

    private void buildPropertysetA(ModelContext context, UUID aId) {
        Propertyset propertyset1 = context.findPropertyset(aId);
        propertyset1.setStringProperty("name", "a");
        propertyset1.setDoubleProperty("value", 2.1);
    }

    private void buildPropertysetB(ModelContext context, UUID bId) {
        Propertyset propertyset1 = context.findPropertyset(bId);
        propertyset1.setStringProperty("name", "b");
        propertyset1.setDoubleProperty("value", 1.2);
    }

    private Propertyset findAspectByTitle(Collection<Propertyset> aspects, String aspectTitle) {
        for (Propertyset aspect : aspects) {
            if (aspectTitle.equals(aspect.getStringProperty("title"))) {
                return aspect;
            }
        }

        return getNilPropertyset();
    }

    private Propertyset buildGeneralObjectAspect(ModelContext context) {
        UUID generalObjectAspectId = UUID.randomUUID();
        Propertyset generalObjectAspect = context.findPropertyset(generalObjectAspectId);
        generalObjectAspect.setStringProperty("title", "general object");
        generalObjectAspect.setStringProperty("aspect", "object");
        Propertyset generalObjectAspectProperties = context.createPropertyset();
        Propertyset nameProperty = context.createPropertyset();
        nameProperty.setStringProperty("aspect", "string");
        generalObjectAspectProperties.setComplexProperty("name", nameProperty);
        Propertyset descriptionProperty = context.createPropertyset();
        descriptionProperty.setStringProperty("aspect", "string");
        generalObjectAspectProperties.setComplexProperty("description", descriptionProperty);
        generalObjectAspect.setComplexProperty("properties", generalObjectAspectProperties);
        return generalObjectAspect;
    }

    private Propertyset buildPositionAspect(ModelContext context) {
        UUID positionAspectId = UUID.randomUUID();
        Propertyset positionAspect = context.findPropertyset(positionAspectId);
        positionAspect.setStringProperty("title", "position");
        positionAspect.setStringProperty("aspect", "object");
        Propertyset positionAspectProperties = context.createPropertyset();
        Propertyset xposProperty = context.createPropertyset();
        xposProperty.setStringProperty("aspect", "number");
        positionAspectProperties.setComplexProperty("xpos", xposProperty);
        Propertyset yposProperty = context.createPropertyset();
        yposProperty.setStringProperty("aspect", "number");
        positionAspectProperties.setComplexProperty("ypos", yposProperty);
        positionAspect.setComplexProperty("properties", positionAspectProperties);
        return positionAspect;
    }

    private void buildModelWithAspects(ModelContext context) {
        // Base aspect "vehicle"
        UUID vechicleAspectId = UUID.randomUUID();
        Propertyset vehicleAspect = context.findPropertyset(vechicleAspectId);
        vehicleAspect.setStringProperty("title", "vehicle");
        vehicleAspect.setStringProperty("aspect", "object");
        Propertyset vehicleAspectProperties = context.createPropertyset();
        Propertyset manufacturerDefinition = context.createPropertyset();
        manufacturerDefinition.setStringProperty("aspect", "string");
        Propertyset modelnameDefinition = context.createPropertyset();
        modelnameDefinition.setStringProperty("aspect", "string");
        vehicleAspectProperties.setComplexProperty("modelname", modelnameDefinition);
        Propertyset wheelCountDefinition = context.createPropertyset();
        wheelCountDefinition.setStringProperty("description", "Number of wheels on the vehicle");
        wheelCountDefinition.setStringProperty("aspect", "integer");
        wheelCountDefinition.setLongProperty("minimum", Long.valueOf(0));
        vehicleAspectProperties.setComplexProperty("definition", wheelCountDefinition);
        vehicleAspect.setComplexProperty("properties", vehicleAspectProperties);

        // Subaspect "bicycle"
        UUID bicycleAspectId = UUID.randomUUID();
        Propertyset bicycleAspect = context.findPropertyset(bicycleAspectId);
        bicycleAspect.setStringProperty("title", "bicycle");
        bicycleAspect.setStringProperty("aspect", "object");
        bicycleAspect.setReferenceProperty("inherits", vehicleAspect);
        Propertyset bicycleAspectProperties = context.createPropertyset();
        Propertyset frameNumber = context.createPropertyset();
        frameNumber.setStringProperty("definition", "Unique identifier for the bicycle");
        bicycleAspectProperties.setComplexProperty("framenumber", frameNumber);
        bicycleAspect.setComplexProperty("properties", bicycleAspectProperties);

        // Subaspect "car"
        UUID carAspectId = UUID.randomUUID();
        Propertyset carAspect = context.findPropertyset(carAspectId);
        carAspect.setStringProperty("title", "car");
        carAspect.setStringProperty("aspect", "object");
        carAspect.setReferenceProperty("inherits", vehicleAspect);
        Propertyset carAspectProperties = context.createPropertyset();
        Propertyset engineSize = context.createPropertyset();
        engineSize.setStringProperty("description", "Engine displacement in litres");
        engineSize.setStringProperty("aspect", "number");
        carAspectProperties.setComplexProperty("engineSize", engineSize);
        Propertyset enginePower = context.createPropertyset();
        enginePower.setStringProperty("description", "Engine power in kW");
        enginePower.setStringProperty("aspect", "number");
        carAspectProperties.setComplexProperty("enginePower", enginePower);
        carAspect.setComplexProperty("properties", carAspectProperties);

        // Make some instances with aspects
        Propertyset head = context.findPropertyset(UUID.randomUUID());
        head.addAspect(bicycleAspect);
        head.setStringProperty("manufacturer", "HEAD");
        head.setStringProperty("model", "Tacoma I");
        head.setStringProperty("frameNumber", "001-234-509-374-331");
        Propertyset nakamura = context.findPropertyset(UUID.randomUUID());
        nakamura.addAspect(bicycleAspect);
        nakamura.setStringProperty("manufacturer", "Nakamura");
        nakamura.setStringProperty("model", "Fatbike 2015");
        nakamura.setStringProperty("frameNumber", "003-577-943-547-931");
        Propertyset ferrari = context.findPropertyset(UUID.randomUUID());
        ferrari.addAspect(carAspect);
        ferrari.setStringProperty("manufacturer", "Ferrari");
        ferrari.setStringProperty("model", "550 Barchetta");
        ferrari.setDoubleProperty("engineSize", 5.5);
        ferrari.setDoubleProperty("enginePower", 357.0);
        Propertyset subaru = context.findPropertyset(UUID.randomUUID());
        subaru.addAspect(carAspect);
        subaru.setStringProperty("manufacturer", "Subaru");
        subaru.setStringProperty("model", "Outback");
        subaru.setDoubleProperty("engineSize", 2.5);
        subaru.setDoubleProperty("enginePower", 125.0);
        Propertyset volvo = context.findPropertyset(UUID.randomUUID());
        volvo.addAspect(carAspect);
        volvo.setStringProperty("manufacturer", "Volvo");
        volvo.setStringProperty("model", "P1800");
        volvo.setDoubleProperty("engineSize", 1.8);
        volvo.setDoubleProperty("enginePower", 84.58);
    }

}
