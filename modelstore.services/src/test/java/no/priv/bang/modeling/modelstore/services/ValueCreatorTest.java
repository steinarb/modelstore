package no.priv.bang.modeling.modelstore.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ValueCreatorTest {

    @Test
    void testService() {
        var service = mock(ValueCreator.class);
        var nilPropertySet = service.getNilPropertyset();
        assertNull(nilPropertySet);
        var nil = service.getNil();
        assertNull(nil);
        var booleanValue1 = service.fromBoolean(Boolean.valueOf(true));
        assertNull(booleanValue1);
        var booleanValue2 = service.fromBoolean(true);
        assertNull(booleanValue2);
        var longValue1 = service.fromLong(Long.valueOf(42L));
        assertNull(longValue1);
        var longValue2 = service.fromLong(42L);
        assertNull(longValue2);
        var doubleValue1 = service.fromDouble(Double.valueOf(3.14));
        assertNull(doubleValue1);
        var doubleValue2 = service.fromDouble(3.14);
        assertNull(doubleValue2);
        var stringValue = service.fromString("foo");
        assertNull(stringValue);
        var propertyset = service.newPropertyset(UUID.randomUUID());
        assertNull(propertyset);
        var complexValue = service.toComplexValue(propertyset);
        assertNull(complexValue);
        var referenceValue = service.toReferenceValue(propertyset);
        assertNull(referenceValue);
        var valueList = service.newValueList();
        assertNull(valueList);
        var valueListValue = service.fromValueList(valueList);
        assertNull(valueListValue);
    }

}
