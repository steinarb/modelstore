package no.priv.bang.modeling.modelstore;


import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static no.priv.bang.modeling.modelstore.testutils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import no.priv.bang.modeling.modelstore.backend.ModelstoreProvider;
import no.priv.bang.modeling.modelstore.services.ModelContext;
import no.priv.bang.modeling.modelstore.services.Modelstore;
import no.priv.bang.modeling.modelstore.value.ValueCreatorProvider;

/**
 * Unit test for the {@link Modelstore} interface and its
 * implementations.
 *
 */
class ModelstoreTest {
    @TempDir
    File folder;

    /**
     * Test fetching the default context of a
     * {@link Modelstore}.
     */
    @Test
    void testGetModelContext() {
        var modelstore = new ModelstoreProvider();
        var valueCreator = new ValueCreatorProvider();
        modelstore.setValueCreator(valueCreator);
        modelstore.activate();
        var context = modelstore.getDefaultContext();
        assertNotNull(context);
        assertEquals(6, context.listAllAspects().size(), "Expected the built-in aspects");
    }

    /**
     * Test loading a {@link ModelContext} from a stream
     * containing a JSON file.
     */
    @Test
    void testRestoreModelContext() {
        var modelstore = new ModelstoreProvider();
        var valueCreator = new ValueCreatorProvider();
        modelstore.setValueCreator(valueCreator);
        modelstore.activate();
        var carsAndBicylesStream = getClass().getResourceAsStream("/json/cars_and_bicycles.json");
        var context = modelstore.restoreContext(carsAndBicylesStream);

        assertEquals(9, context.listAllAspects().size());
        assertEquals(9, context.listAllPropertysets().size());
    }

    /**
     * Test saving a {@link ModelContext} to a file stream
     * and then restoring the file into a different context.
     * @throws Exception
     */
    @Test
    void testPersistRestoreModelContext() throws Exception { // NOSONAR assert inside compareAllPropertysets
        var modelstore = new ModelstoreProvider();
        var valueCreator = new ValueCreatorProvider();
        modelstore.setValueCreator(valueCreator);
        modelstore.activate();
        var carsAndBicylesStream = getClass().getResourceAsStream("/json/cars_and_bicycles.json");
        var context1 = modelstore.restoreContext(carsAndBicylesStream);

        var saveFile = new File(folder, "save");
        var saveStream = Files.newOutputStream(saveFile.toPath());
        modelstore.persistContext(saveStream, context1);

        var loadStream = Files.newInputStream(saveFile.toPath());
        var context2 = modelstore.restoreContext(loadStream);

        compareAllPropertysets(context1, context2);
    }

    /**
     * Test saving a {@link ModelContext} to a {@link PipedOutputStream}
     * and then restoring the context from a {@link PipedInputStream}.
     *
     * Have to write to the pipe from a different thread than the reader,
     * or else it will deadlock.
     *
     * @throws IOException
     */
    @Test
    void testPersistRestoreModelContextUsingPipedStreams() throws Exception { // NOSONAR assert inside compareAllPropertysets
        var modelstore = new ModelstoreProvider();
        var valueCreator = new ValueCreatorProvider();
        modelstore.setValueCreator(valueCreator);
        modelstore.activate();
        var carsAndBicylesStream = getClass().getResourceAsStream("/json/cars_and_bicycles.json");
        final var context1 = modelstore.restoreContext(carsAndBicylesStream);

        var loadStream = new PipedInputStream();
        final var saveStream = new PipedOutputStream(loadStream);
        new Thread(new Runnable() {
            public void run() {
                modelstore.persistContext(saveStream, context1);
            }
        }).start();

        var context2 = modelstore.restoreContext(loadStream);

        compareAllPropertysets(context1, context2);
    }

    /**
     * Try logging from two threads to reveal race conditions.
     * One thread log 10000 entries with 1ms interval
     *
     * The main thread waits a couple of milliseconds, then logs
     * one entry, gets the list of errors, logs another error,
     * then gets the list of error 1000 times with 2ms intervals.
     *
     * Then the threads are joined and let list of errors should
     * have a size of 10002.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void testLogMultithreading() throws Exception {
        final var modelstore = new ModelstoreProvider();

        var other = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 500; i++) {
                    var message = "Error in other thread " + i;
                    modelstore.logError(message, null, null);
                }
            }
        });
        other.start();

        modelstore.logError("Error in this thread 1", null, null);
        assertThat(modelstore.getErrors()).hasSize(1);
        modelstore.logError("Error in this thread 2", null, null);
        for (var i = 0; i < 500; i++) {
            modelstore.logError("Error in this thread " + i, null, null);
        }

        other.join();

        // Verify that the expected number of log messages have been logged
        assertThat(modelstore.getErrors()).hasSize(1002);
    }

}
