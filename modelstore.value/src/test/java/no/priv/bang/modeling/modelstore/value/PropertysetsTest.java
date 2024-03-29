package no.priv.bang.modeling.modelstore.value;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static no.priv.bang.modeling.modelstore.value.Propertysets.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import no.priv.bang.modeling.modelstore.services.ModificationRecorder;
import no.priv.bang.modeling.modelstore.services.Propertyset;

/**
 * Unit tests for class {@link Propertysets}.
 *
 */
class PropertysetsTest {
    /**
     * Unit tests for {@link Propertysets#findWrappedPropertyset(no.priv.bang.modeling.modelstore.Propertyset)}.
     */
    @Test
    void testFindWrappedPropertyset() {
        var recorder = mock(ModificationRecorder.class);
        var valueCreator = new ValueCreatorProvider();
        var inner = valueCreator.newPropertyset(UUID.randomUUID());

        // Test unwrapping of a wrapped propertyset
        var wrappedPropertyset = new PropertysetRecordingSaveTime(recorder, inner);
        Propertyset unwrappedPropertyset = findWrappedPropertyset(wrappedPropertyset);
        assertNotSame(wrappedPropertyset, unwrappedPropertyset); // Not the same object
        assertEquals(wrappedPropertyset, unwrappedPropertyset); // A wrapper tests equal to the propertyset it wraps

        // Test that an unwrapped propertyset comes through as itself
        Propertyset unwrappedPropertyset2 = findWrappedPropertyset(unwrappedPropertyset);
        assertSame(unwrappedPropertyset, unwrappedPropertyset2);
    }
}
