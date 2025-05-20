package com.softhouse.technicaltests.peopleporterpipeline.processors;

import com.softhouse.technicaltests.peopleporterpipeline.domain.Person;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A Camel {@link org.apache.camel.Processor} that performs streaming XML writing of individual
 * {@link com.softhouse.technicaltests.peopleporterpipeline.domain.Person} objects using JAXB.
 * <p>
 * <h2>Concurrency and Thread Safety</h2>
 * <ul>
 *   <li>A static {@code WRITE_LOCK} ensures that multiple threads writing concurrently do not corrupt the output.</li>
 * </ul>
 *
 * <h2>Output File</h2>
 * <ul>
 *   <li>The output file is hardcoded as {@code camel/output/people.xml}.</li>
 * </ul>
 */
public class StreamingXmlWriterProcessor implements Processor {

    private static final Path OUTPUT_PATH = Path.of("camel/output/people.xml");
    private static final Lock WRITE_LOCK = new ReentrantLock();
    private static final JAXBContext CONTEXT;

    static {
        try {
            CONTEXT = JAXBContext.newInstance(Person.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JAXBContext", e);
        }

        try (var out = new BufferedOutputStream(new FileOutputStream(OUTPUT_PATH.toFile(), false))) {
            out.write("<people>\n".getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize output file", e);
        }
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Person person = exchange.getMessage().getBody(Person.class);
        File outputFile = OUTPUT_PATH.toFile();

        WRITE_LOCK.lock();
        try (var out = new BufferedOutputStream(new FileOutputStream(outputFile, true))) {
            Marshaller marshaller = CONTEXT.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE); // omit <?xml...>
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(person, out);
        } finally {
            WRITE_LOCK.unlock();
        }
    }
}
