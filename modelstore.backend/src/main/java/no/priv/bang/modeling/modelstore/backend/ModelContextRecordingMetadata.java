package no.priv.bang.modeling.modelstore.backend;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import no.priv.bang.modeling.modelstore.services.DateFactory;
import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.ModificationRecorder;
import no.priv.bang.modeling.modelstore.services.Propertyset;
import no.priv.bang.modeling.modelstore.services.ValueCreator;
import no.priv.bang.modeling.modelstore.services.ValueList;

import static no.priv.bang.modeling.modelstore.backend.Aspects.*;
import static no.priv.bang.modeling.modelstore.backend.ModelContexts.*;

public class ModelContextRecordingMetadata implements ModelContext, ModificationRecorder {

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    private ModelContext impl;
    private DateFactory dateFactory;
    private ValueCreator valueCreator;
    private Map<UUID,Date> lastmodifiedtime = new HashMap<>();

    public ModelContextRecordingMetadata(ModelContext nonMetadataRecordingContext, DateFactory dateFactory, ValueCreator valueCreator) {
        impl = nonMetadataRecordingContext;
        this.dateFactory = dateFactory;
        this.valueCreator = valueCreator;
        var metadata = impl.findPropertyset(metadataId);
        setLastmodifiedtimes(metadata);
    }

    /**
     * UUIDs of propertysets are the propertynames of the "lastmodifiedtimes"
     * complex property.  The values are strings containing {@link Date} values
     * from the propertyset's last modified date.
     *
     * @param metadata a {@link Propertyset} to extract saved last modified times from
     */
    void setLastmodifiedtimes(Propertyset metadata) {
        var lastmodifiedtimes = metadata.getComplexProperty("lastmodifiedtimes");
        for (var uuidAsString : lastmodifiedtimes.getPropertynames()) {
            var dateAsString = lastmodifiedtimes.getStringProperty(uuidAsString);
            try {
                lastmodifiedtime.put(UUID.fromString(uuidAsString), dateFormat.parse(dateAsString));
            } catch (IllegalArgumentException e) {
                logError("Metadata \"lastmodifiedtime\" Propertyset id not parsable as a UUID", uuidAsString, e);
            } catch (ParseException e) {
                logError("Metadata \"lastmodifiedtime\" value not parsable as a Date", dateAsString, e);
            }
        }
    }

    ModelContext getWrappedModelContext() {
        return impl;
    }

    public ValueList createList() {
        return impl.createList();
    }

    public Propertyset createPropertyset() {
        return impl.createPropertyset();
    }

    public Propertyset findPropertyset(UUID id) {
        return valueCreator.wrapInModificationTracker(this, impl.findPropertyset(id));
    }

    public Collection<Propertyset> listAllPropertysets() {
        var implist = impl.listAllPropertysets();
        var retlist = new ArrayList<Propertyset>(implist.size() + 1);
        retlist.add(createMetadata());
        for (var propertyset : implist) {
            if (!metadataId.equals(propertyset.getId())) {
                retlist.add(propertyset);
            }
        }

        return retlist;
    }

    public Collection<Propertyset> listAllAspects() {
        return impl.listAllAspects();
    }

    public Collection<Propertyset> findObjectsOfAspect(Propertyset aspect) {
        return impl.findObjectsOfAspect(valueCreator.unwrapPropertyset(aspect));
    }

    /**
     * Set a timestamp for the propertyset given as an argument
     *
     * @param propertyset the {@link Propertyset} to set a timestamp for
     */
    public void modifiedPropertyset(Propertyset propertyset) {
        lastmodifiedtime.put(propertyset.getId(), dateFactory.now());
    }

    public Date getLastmodifieddate(Propertyset propertyset) {
        return lastmodifiedtime.get(propertyset.getId());
    }

    private Propertyset createMetadata() {
        var lastmodifiedtimes = impl.createPropertyset();
        for (var modifiedtime : lastmodifiedtime.entrySet()) {
            lastmodifiedtimes.setStringProperty(modifiedtime.getKey().toString(), dateFormat.format(modifiedtime.getValue()));
        }

        Propertyset metadata = impl.findPropertyset(metadataId);
        metadata.addAspect(findPropertyset(metadataAspectId));
        metadata.setComplexProperty("lastmodifiedtimes", lastmodifiedtimes);
        return metadata;
    }

    public void merge(ModelContext otherContext) {
        ModelContexts.merge(this, otherContext);
    }

    @Override
    public int hashCode() {
        final var prime = 31;
        var result = 1;
        result = prime * result + impl.hashCode();
        result = prime * result + lastmodifiedtime.hashCode();
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

        var other = (ModelContextRecordingMetadata) obj;
        if (!impl.equals(other.impl)) {
            return false;
        }

        return lastmodifiedtime.equals(other.lastmodifiedtime);
    }

    @Override
    public String toString() {
        return "ModelContextRecordingMetadata [impl=" + impl + "]";
    }

    public void logError(String message, Object fileOrStream, Exception e) {
        impl.logError(message, fileOrStream, e);
    }

}
