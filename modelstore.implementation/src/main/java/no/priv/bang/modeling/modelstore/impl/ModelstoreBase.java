package no.priv.bang.modeling.modelstore.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Provider;

import com.fasterxml.jackson.core.JsonFactory;

import no.priv.bang.modeling.modelstore.ErrorBean;
import no.priv.bang.modeling.modelstore.ModelContext;
import no.priv.bang.modeling.modelstore.Modelstore;

/**
 * Class implementing {@link Modelstore} for use as a base
 * class for {@link Provider} classes for {@link Modelstore}
 *
 * @author Steinar Bang
 *
 */
class ModelstoreBase extends BuiltinAspectsBase implements Modelstore {

    private ModelContext context = new ModelContextImpl(this);
    private List<ErrorBean> errors = Collections.synchronizedList(new ArrayList<ErrorBean>());

    protected ModelstoreBase() {
    }

    public ModelContext getDefaultContext() {
        return context;
    }

    public ModelContext createContext() {
        ModelContextImpl ctxt = new ModelContextImpl(this);
        return new ModelContextRecordingMetadata(ctxt);
    }

    public ModelContext restoreContext(InputStream jsonfilestream) {
        ModelContextImpl ctxt = new ModelContextImpl(this);
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);
        persister.restore(jsonfilestream, ctxt);

        return new ModelContextRecordingMetadata(ctxt);
    }

    public void persistContext(OutputStream jsonfilestream, ModelContext context) {
        JsonFactory jsonFactory = new JsonFactory();
        JsonPropertysetPersister persister = new JsonPropertysetPersister(jsonFactory);
        persister.persist(jsonfilestream, context);
    }

    public void logError(String message, Object fileOrStream, Exception execption) {
        errors.add(new ErrorBean(new Date(), message, fileOrStream, execption));
    }

    public List<ErrorBean> getErrors() {
        synchronized (errors) {
            // Defensive copy
            return new ArrayList<ErrorBean>(errors);
        }
    }

}
