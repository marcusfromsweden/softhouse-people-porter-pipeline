package com.softhouse.technicaltests.peopleporterpipeline.processors;

import com.softhouse.technicaltests.peopleporterpipeline.common.LineType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A Camel {@link Processor} that reads an {@link InputStream} and splits it into an {@link Iterable}
 * of person blocks (multi-line strings). Each block starts with a P| line and ends before the next.
 * <p>
 * This version is streaming and memory-efficient: it returns an Iterable that lazily reads and splits the file.
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
