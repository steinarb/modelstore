package no.priv.bang.modeling.modelstore;


import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.util.List;

import static no.priv.bang.modeling.modelstore.testutils.TestUtils.*;
import no.priv.bang.modeling.modelstore.impl.ModelstoreProvider;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit test for the {@link Modelstore} interface and its
 * implementations.
 *
 * @author Steinar Bang
 *
 */
public class ModelstoreTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Test fetching the default context of a
     * {@link Modelstore}.
     */
    @Test
    public void testGetModelContext() {
        Modelstore modelstore = new ModelstoreProvider().get();
        ModelContext context = modelstore.getDefaultContext();
        assertNotNull(context);
        assertEquals("Expected the built-in aspects", 6, context.listAllAspects().size());
    }

    /**
     * Test loading a {@link ModelContext} from a stream
     * containing a JSON file.
     */
    @Test
    public void testRestoreModelContext() {
    	Modelstore modelstore = new ModelstoreProvider().get();
    	InputStream carsAndBicylesStream = getClass().getResourceAsStream("/json/cars_and_bicycles.json");
    	ModelContext context = modelstore.restoreContext(carsAndBicylesStream);

    	assertEquals(9, context.listAllAspects().size());
        assertEquals(9, context.listAllPropertysets().size());
    }

    /**
     * Test saving a {@link ModelContext} to a file stream
     * and then restoring the file into a different context.
     * @throws IOException
     */
    @Test
    public void testPersistRestoreModelContext() throws IOException {
        Modelstore modelstore = new ModelstoreProvider().get();
        InputStream carsAndBicylesStream = getClass().getResourceAsStream("/json/cars_and_bicycles.json");
        ModelContext context1 = modelstore.restoreContext(carsAndBicylesStream);

        File saveFile = folder.newFile();
        OutputStream saveStream = Files.newOutputStream(saveFile.toPath());
        modelstore.persistContext(saveStream, context1);

        InputStream loadStream = Files.newInputStream(saveFile.toPath());
        ModelContext context2 = modelstore.restoreContext(loadStream);

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
    public void testPersistRestoreModelContextUsingPipedStreams() throws IOException {
        final Modelstore modelstore = new ModelstoreProvider().get();
        InputStream carsAndBicylesStream = getClass().getResourceAsStream("/json/cars_and_bicycles.json");
        final ModelContext context1 = modelstore.restoreContext(carsAndBicylesStream);

        PipedInputStream loadStream = new PipedInputStream();
        final OutputStream saveStream = new PipedOutputStream(loadStream);
        new Thread(new Runnable() {
                public void run() {
                    modelstore.persistContext(saveStream, context1);
                }
            }).start();

        ModelContext context2 = modelstore.restoreContext(loadStream);

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
    public void testLogMultithreading() throws IOException, InterruptedException {
        final Modelstore modelstore = new ModelstoreProvider().get();

        Thread other = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 500; i++) {
                        String message = "Error in other thread " + i;
                        modelstore.logError(message, null, null);
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) { }
                    }
                }
            });
        other.start();

        // Throw a little sand in the works in this thread
        Thread.sleep(10);
        modelstore.logError("Error in this thread 1", null, null);
        List<ErrorBean> temp = modelstore.getErrors();
        assertNotNull(temp);
        modelstore.logError("Error in this thread 2", null, null);
        for (int i = 0; i < 500; i++) {
            modelstore.logError("Error in this thread " + i, null, null);
            temp = modelstore.getErrors();
        }

        other.join();

        // Verify that the expected number of log messages have been logged
        assertEquals(1002, modelstore.getErrors().size());
    }

}
