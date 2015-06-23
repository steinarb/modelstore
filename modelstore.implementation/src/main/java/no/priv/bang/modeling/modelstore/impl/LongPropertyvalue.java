package no.priv.bang.modeling.modelstore.impl;

import no.priv.bang.modeling.modelstore.Propertyset;
import no.priv.bang.modeling.modelstore.PropertysetNil;
import no.priv.bang.modeling.modelstore.PropertyvalueList;

/**
 * Wraps a {@link Long} value in a {@link Propertyset}.
 *
 * Will return well defined values if asked to return a different
 * property type.
 *
 * @author Steinar Bang
 *
 */
class LongPropertyvalue extends PropertyvalueBase {
    private Long value;

    /**
     * Create new instances of {@link LongPropertyvalue}.
     *
     * @param value the value to wrap
     * @deprecated use {@link Propertyvalues#toLongValue(Long)} instead
     */
    public LongPropertyvalue(Long value) {
    	if (null == value) {
            this.value = PropertyvalueNil.getNil().asLong();
    	} else {
            this.value = value;
    	}
    }

    public boolean isLong() {
        return true;
    }

    public Boolean asBoolean() {
        long intValue = value.longValue();
        return (intValue != 0);
    }

    public Long asLong() {
        return value;
    }

    public Double asDouble() {
        long intValue = value.longValue();
        return (double) intValue;
    }

    public String asString() {
        return value.toString();
    }

    public Propertyset asComplexProperty() {
        return PropertysetNil.getNil();
    }

    public Propertyset asReference() {
        return PropertysetNil.getNil();
    }

    public PropertyvalueList asList() {
        return PropertyvalueNil.getNil().asList();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + value.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        LongPropertyvalue other = (LongPropertyvalue) obj;
        return value.equals(other.value);
    }

    @Override
    public String toString() {
        return "LongPropertyvalue [value=" + value + "]";
    }

}
