package no.priv.bang.modeling.modelstore.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Propertyset;

/**
 * Common code and constants for {@link ModelContext} implementations.
 *
 */
public class ModelContexts {

    // A reserved ID used by the metadata object.
    static final UUID metadataId = UUID.fromString("b1ad694b-4003-412b-8249-a7d1a0a24cf3");

    public static ModelContext findWrappedModelContext(ModelContext modelcontext) {
        if (modelcontext instanceof ModelContextRecordingMetadata) {
            var outer = (ModelContextRecordingMetadata) modelcontext;
            return outer.getWrappedModelContext();
        }

        return modelcontext;
    }

    /**
     * Merge two {@link ModelContext} objects.  The merge results will be the first
     * context.
     *
     * Each {@link Propertyset} in the contexts will be merged separately.
     * If a propertyset in otherContext doesn't exist in context it will
     * be copied into context.  The primitive values are immutable and
     * will be reused. Complex properties and list properties will be
     * copied to avoid thread synchronization issues.
     *
     * {@link Propertyset} instances that exist in both contexts will
     * result in the values of the {@link Propertyset} instance in one
     * context being copied into the {@link Propertyset} instance in
     * the other context. Which {@link Propertyset} is copied into and
     * which is copied from, depends on the lastmodified time of
     * the {@link Propertyset} (newest {@link Propertyset} is copied
     * from and oldest {@link Propertyset} is copied into, potentially
     * overwriting properties with the same name).
     *
     * For {@link ModelContext} implementations with no lastmodifiedtime
     * information otherContext propertysets will be copied into context
     * propertysets.
     *
     * @param context the context that will contain the merge results
     * @param otherContext the context that is merged into the other context, this context is unchanged after the merge
     */
    public static void merge(ModelContext context, ModelContext otherContext) {
        if (otherContext == null) {
            // No point in testing for null in the first argument, because this
            // will always be "this" when called from a ModelContext implementation
            // and if used in a different way, must be done from inside this package
            // and it will be best to throw the exception during development
            return; // nothing to merge with, context is unchanged
        }

        var propertysetIds = findPropertysetIds(context.listAllPropertysets());
        for (var propertyset : otherContext.listAllPropertysets()) {
            var propertysetId = propertyset.getId();
            if (!metadataId.equals(propertysetId)) {
                if (!propertysetIds.contains(propertysetId)) {
                    addMissingPropertysetToContext(context, propertysetId, propertyset);
                } else {
                    // Merge the properties, let most recently modified propertyset overwrite values with the same name in the other propertyset
                    var contextPropertyset = context.findPropertyset(propertysetId);
                    var contextPropertysetLastmodifiedtime = context.getLastmodifieddate(contextPropertyset);
                    var otherContextPropertysetLastModifiedTime = otherContext.getLastmodifieddate(propertyset);
                    if (otherContextPropertysetLastModifiedTime.compareTo(contextPropertysetLastmodifiedtime) >= 0) {
                        contextPropertyset.copyValues(propertyset);
                    } else {
                        var savedOriginalValues = context.createPropertyset();
                        savedOriginalValues.copyValues(contextPropertyset);
                        contextPropertyset.copyValues(propertyset);
                        contextPropertyset.copyValues(savedOriginalValues); // overwrite with original
                    }
                }
            }
        }
    }

    private static void addMissingPropertysetToContext(ModelContext context, UUID propertysetId, Propertyset propertyset) {
        var newPropertyset = context.findPropertyset(propertysetId);
        newPropertyset.copyValues(propertyset);
    }

    private static Collection<UUID> findPropertysetIds(Collection<Propertyset> propertysets) {
        var propertysetIds = new ArrayList<UUID>(propertysets.size()-1);
        for (var propertyset : propertysets) {
            var propertysetId = propertyset.getId();
            if (!metadataId.equals(propertysetId)) {
                propertysetIds.add(propertysetId);
            }
        }

        return propertysetIds;
    }

    private ModelContexts() { /* static class */ }

}
