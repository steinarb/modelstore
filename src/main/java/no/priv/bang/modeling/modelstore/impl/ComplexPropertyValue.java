package no.priv.bang.modeling.modelstore.impl;

import no.priv.bang.modeling.modelstore.Propertyset;
import no.priv.bang.modeling.modelstore.PropertysetNil;

public class ComplexPropertyValue extends PropertysetPropertyValueBase {

    public ComplexPropertyValue(Propertyset value) {
    	super(value);
    }

    @Override
    public boolean isComplexProperty() {
        return true;
    }

    public Propertyset asComplexProperty() {
        return value;
    }

    public Propertyset asReferenceProperty() {
        return PropertysetNil.getNil();
    }

}
