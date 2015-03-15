package no.priv.bang.modeling.modelstore.impl;

import no.priv.bang.modeling.modelstore.Propertyset;
import no.priv.bang.modeling.modelstore.PropertysetNil;

/**
 * Wraps a {@link Double} value in a {@link Propertyset}.
 *
 * Will return well defined values if asked to return a different
 * property type.
 *
 * @author Steinar Bang
 *
 */
class DoublePropertyvalue extends PropertyvalueBase {
    private Double value;

    public DoublePropertyvalue(Double value) {
        this.value = value;
    }

    public boolean isDouble() {
        return true;
    }

    public Boolean asBoolean() {
        double floatValue = value.doubleValue();
        return floatValue != 0.0;
    }

    public Long asLong() {
        double floatValue = value.doubleValue();
        return Math.round(floatValue);
    }

    public Double asDouble() {
        return value;
    }

    public String asString() {
        return value.toString();
    }

    public Propertyset asComplexProperty() {
        return PropertysetNil.getNil();
    }

    public Propertyset asReferenceProperty() {
        return PropertysetNil.getNil();
    }

}