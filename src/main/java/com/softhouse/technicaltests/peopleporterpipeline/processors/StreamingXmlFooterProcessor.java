package com.softhouse.technicaltests.peopleporterpipeline.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A Camel {@link org.apache.camel.Processor} that writes the closing {@code </people>} XML tag
 * to the output file once all {@code <person>} entries have been processed.
 * <p>
 * <h2>Thread Safety</h2>
 * <ul>
 *   <li>A static {@link ReentrantLock} is used to ensure exclusive write access to the output file.</li>
 *   <li>This prevents multiple threads from writing simultaneously, which is critical in scenarios
 *       involving {@code parallelProcessing()} or other concurrency mechanisms.</li>
 * </ul>
 */
public class StreamingXmlFooterProcessor implements Processor {

    private static final Path OUTPUT_PATH = Path.of("camel/output/people.xml");
    private static final Lock WRITE_LOCK = new ReentrantLock();

    @Override
    public void process(Exchange exchange) throws Exception {
        WRITE_LOCK.lock();
        try (var out = new BufferedOutputStream(new FileOutputStream(OUTPUT_PATH.toFile(), true))) {
            out.write("</people>\n".getBytes());
        } finally {
            WRITE_LOCK.unlock();
        }
    }
}
