package no.priv.bang.modeling.modelstore;

import java.util.Collection;
import java.util.UUID;

/**
 * Interface defining a class that can function as both nodes
 * and edges in a model graph.
 *
 * The graph shold be easy to work and there should be no
 * need to check for null values: the typed property accessors
 * will never return null.
 *
 * The raw property accessors will return null values for unset
 * properties, but the raw accessors are mainly intended for reflection.
 *
 * @author Steinar Bang
 *
 */
public interface Propertyset {

    boolean isNil();

    Collection<String> getPropertynames();

    Value getProperty(String propertyname);

    void setProperty(String propertyname, Value property);

    void addAspect(Propertyset aspect);

    boolean hasAspect();

    ValueList getAspects();

    boolean hasId();

    UUID getId();

    Boolean getBooleanProperty(String propertyname);
    void setBooleanProperty(String propertyname, Boolean boolValue);
    void setBooleanProperty(String propertyname, boolean boolValue);

    Long getLongProperty(String propertyname);
    void setLongProperty(String propertyname, Long intValue);
    void setLongProperty(String propertyname, long intvalue);

    Double getDoubleProperty(String propertyname);
    void setDoubleProperty(String propertyname, Double doubleValue);
    void setDoubleProperty(String propertyname, double doubleValue);

    String getStringProperty(String propertyname);
    void setStringProperty(String propertyname, String stringValue);

    Propertyset getComplexProperty(String propertyname);
    void setComplexProperty(String propertyname, Propertyset complexProperty);

    Propertyset getReferenceProperty(String propertyname);
    void setReferenceProperty(String propertyname, Propertyset referencedObject);

    ValueList getListProperty(String propertyname);
    void setListProperty(String propertyname, ValueList listValue);

}