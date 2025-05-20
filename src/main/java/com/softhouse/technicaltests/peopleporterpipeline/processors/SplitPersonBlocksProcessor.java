package com.softhouse.technicaltests.peopleporterpipeline.processors;

import com.softhouse.technicaltests.peopleporterpipeline.common.LineType;
import com.softhouse.technicaltests.peopleporterpipeline.domain.Person;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A Camel {@link org.apache.camel.Processor} responsible for serializing individual {@link Person} objects
 * to an XML file using JAXB in a streaming, append-only fashion.
 * <p>
 * <h2>Streaming Output</h2>
 * <ul>
 *   <li>The output file is written to incrementally, avoiding the need for in-memory aggregation.</li>
 *   <li>The file begins with a {@code <people>} root element, which is initialized statically in a static block.</li>
 *   <li>The corresponding closing tag {@code </people>} must be written separately using {@link StreamingXmlFooterProcessor}.</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <ul>
 *   <li>To support parallel processing, a static {@link ReentrantLock} is used to ensure only one thread writes
 *       to the file at a time.</li>
 * </ul>
 * <p>
 * <h2>File Behavior</h2>
 * <ul>
 *   <li>The file {@code camel/output/people.xml} is created or overwritten at startup, and then
 *       each {@link Person} is appended during route processing.</li>
 * </ul>
 */

public class SplitPersonBlocksProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        InputStream inputStream = exchange.getIn().getBody(InputStream.class);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Iterable<String> personBlocks = createPersonBlockIterable(reader);

        exchange.getIn().setBody(personBlocks);
    }

    private Iterable<String> createPersonBlockIterable(BufferedReader reader) {
        return () -> new Iterator<>() {
            private String nextLine;
            private boolean hasMore = true;

            @Override
            public boolean hasNext() {
                if (!hasMore) return false;
                try {
                    if (nextLine == null) {
                        nextLine = reader.readLine();
                        if (nextLine == null) {
                            hasMore = false;
                        }
                    }
                    return nextLine != null;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public String next() {
                if (!hasNext()) throw new NoSuchElementException();

                List<String> block = new ArrayList<>();
                try {
                    block.add(nextLine);
                    nextLine = null;

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (isPersonLine(line)) {
                            nextLine = line; // save for next call
                            break;
                        }
                        block.add(line);
                    }

                    if (line == null) {
                        hasMore = false;
                    }

                    return String.join(System.lineSeparator(), block);

                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
    }

    private static boolean isPersonLine(String line) {
        return line != null && line.startsWith(LineType.PERSON + "|");
    }
}
