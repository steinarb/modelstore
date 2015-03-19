package no.priv.bang.modeling.modelstore.impl;

import java.util.HashMap;
import java.util.Map;

import no.priv.bang.modeling.modelstore.Propertyset;
import no.priv.bang.modeling.modelstore.PropertysetNil;
import no.priv.bang.modeling.modelstore.Propertyvalue;
import no.priv.bang.modeling.modelstore.PropertyvalueList;

/**
 * Implementation of {@link Propertyset} backed by a {@link Map}.
 *
 * @author Steinar Bang
 *
 */
public class PropertysetImpl implements Propertyset {
    Map<String, Propertyvalue> properties = new HashMap<String, Propertyvalue>();

    public boolean isNil() {
        return false;
    }

    public Boolean getBooleanProperty(String propertyName) {
        Propertyvalue rawPropertyValue = properties.get(propertyName);
        if (null != rawPropertyValue) {
            return rawPropertyValue.asBoolean();
        }

        return PropertysetNil.getNil().getBooleanProperty(propertyName);
    }

    public void setBooleanProperty(String propertyName, Boolean boolValue) {
        properties.put(propertyName, new BooleanPropertyvalue(boolValue));
    }

    public Long getLongProperty(String propertyName) {
        Propertyvalue rawPropertyValue = properties.get(propertyName);
        if (null != rawPropertyValue) {
            return rawPropertyValue.asLong();
        }

        return PropertysetNil.getNil().getLongProperty(propertyName);
    }

    public void setLongProperty(String propertyName, Long intValue) {
        properties.put(propertyName, new LongPropertyvalue(intValue));
    }

    public Double getDoubleProperty(String propertyName) {
        Propertyvalue rawPropertyValue = properties.get(propertyName);
        if (null != rawPropertyValue) {
            return rawPropertyValue.asDouble();
        }

        return PropertysetNil.getNil().getDoubleProperty(propertyName);
    }

    public void setDoubleProperty(String propertyName, Double doubleValue) {
        properties.put(propertyName, new DoublePropertyvalue(doubleValue));
    }

    public String getStringProperty(String propertyName) {
        Propertyvalue rawPropertyValue = properties.get(propertyName);
        if (null != rawPropertyValue) {
            return rawPropertyValue.asString();
        }

        return PropertysetNil.getNil().getStringProperty(propertyName);
    }

    public void setStringProperty(String propertyName, String stringValue) {
        properties.put(propertyName, new StringPropertyvalue(stringValue));
    }

    public Propertyset getComplexProperty(String propertyName) {
    	Propertyvalue rawPropertyValue = properties.get(propertyName);
    	if (null != rawPropertyValue) {
            return rawPropertyValue.asComplexProperty();
    	}

        return PropertysetNil.getNil();
    }

    public void setComplexProperty(String propertyName, Propertyset complexProperty) {
        properties.put(propertyName, new ComplexPropertyvalue(complexProperty));
    }

    public Propertyset getReferenceProperty(String propertyName) {
    	Propertyvalue rawPropertyValue = properties.get(propertyName);
    	if (null != rawPropertyValue) {
            return rawPropertyValue.asReference();
    	}

        return PropertysetNil.getNil();
    }

    public void setReferenceProperty(String propertyName, Propertyset referencedObject) {
    	properties.put(propertyName, new ReferencePropertyvalue(referencedObject));
    }

    public PropertyvalueList getListProperty(String propertyName) {
    	Propertyvalue rawPropertyValue = properties.get(propertyName);
    	if (null != rawPropertyValue) {
            return rawPropertyValue.asList();
    	}

        return PropertysetNil.getNil().getListProperty(propertyName);
    }

    public void setListProperty(String propertyName, PropertyvalueList listValue) {
        properties.put(propertyName, new ListPropertyvalue(listValue));
    }

}
