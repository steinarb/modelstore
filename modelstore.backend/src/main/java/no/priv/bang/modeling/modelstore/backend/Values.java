package no.priv.bang.modeling.modelstore.backend;

import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.Value;
import no.priv.bang.modeling.modelstore.services.ValueList;

/**
 * A class with static methods for wrapping Java property values
 * in {@link Value} objects.
 *
 * @author Steinar Bang
 *
 */
@SuppressWarnings("deprecation")
public class Values {

    /**
     * Get the singleton {@link PropertysetNil} object.
     *
     * @return a reference to {@link PropertysetNil}.
     */
    static public Propertyset getNilPropertyset() {
        return PropertysetNil.getNil();
    }

    /**
     * Get the singleton {@link NilValue} object.
     *
     * @return a reference to {@link NilValue}.
     */
    static public Value getNil() {
        return NilValue.getNil();
    }

    /**
     * Create new instances of {@link BooleanValue}
     *
     * @param boolValue the value to wrap
     * @return a {@link BooleanValue} instance
     */
    static public Value toBooleanValue(Boolean boolValue) {
        return new BooleanValue(boolValue);
    }

    /**
     * Create new instances of {@link BooleanValue}
     *
     * @param boolValue the value to wrap
     * @return a {@link BooleanValue} instance
     */
    static public Value toBooleanValue(boolean boolValue) {
        return toBooleanValue(Boolean.valueOf(boolValue));
    }

    /**
     * Create new instances of {@link LongValue}
     *
     * @param intValue the value to wrap
     * @return a {@link LongValue} instance
     */
    static public Value toLongValue(Long intValue) {
        return new LongValue(intValue);
    }

    /**
     * Create new instances of {@link LongValue}
     *
     * @param intValue the value to wrap
     * @return a {@link LongValue} instance
     */
    static public Value toLongValue(long intValue) {
        return toLongValue(Long.valueOf(intValue));
    }

    /**
     * Create new instances of {@link DoubleValue}
     *
     * @param doubleValue the value to wrap
     * @return a {@link DoubleValue} instance
     */
    static public Value toDoubleValue(Double doubleValue) {
        return new DoubleValue(doubleValue);
    }

    /**
     * Create new instances of {@link DoubleValue}
     *
     * @param doubleValue the value to wrap
     * @return a {@link DoubleValue} instance
     */
    static public Value toDoubleValue(double doubleValue) {
        return toDoubleValue(Double.valueOf(doubleValue));
    }

    /**
     * Create new instances of {@link StringValue}
     *
     * @param stringValue the value to wrap
     * @return a {@link StringValue} instance
     */
    static public Value toStringValue(String stringValue) {
        return new StringValue(stringValue);
    }

    /**
     * Create new instances of {@link ComplexValue}
     *
     * @param complexValue the value to wrap
     * @return a {@link ComplexValue} instance
     */
    static public Value toComplexValue(Propertyset complexValue) {
        return toComplexValue(complexValue, true);
    }

    /**
     * Create new instances of {@link ComplexValue}
     *
     * @param complexValue the value to wrap
     * @param makeDefensiveCopy if true, create a copy of the complexValue and give the copy to the value
     * @return a {@link ComplexValue} instance
     */
    static public Value toComplexValue(Propertyset complexValue, boolean makeDefensiveCopy) {
        Propertyset value = makeDefensiveCopy && complexValue != null ? new PropertysetImpl(complexValue) : complexValue;
        return new ComplexValue(value);
    }

    /**
     * Create new instances of {@link ReferenceValue}
     *
     * @param referencedValue the value to wrap
     * @return a {@link ReferenceValue} instance
     */
    static public Value toReferenceValue(Propertyset referencedValue) {
        return new ReferenceValue(referencedValue);
    }

    /**
     * Create new instances of {@link ListValue}
     *
     * @param listValue the value to wrap
     * @return a {@link ListValue} instance
     */
    static Value toListValue(ValueList listValue) {
        return toListValue(listValue, true);
    }

    /**
     * Create new instances of {@link ListValue}
     *
     * @param listValue the value to wrap
     * @param makeDefensiveCopy if true, create a copy of the list and give the copy to the value
     * @return a {@link ListValue} instance
     */
    static Value toListValue(ValueList listValue, boolean makeDefensiveCopy) {
        ValueList value = makeDefensiveCopy && listValue != null ? new ValueArrayList(listValue) : listValue;
        return new ListValue(value);
    }

    /**
     * Create new instances of {@link ValueArrayList}.
     *
     * @return a new empty instance of {@link ValueArrayList}.
     */
    static public ValueList newList() {
        return new ValueArrayList();
    }

}
