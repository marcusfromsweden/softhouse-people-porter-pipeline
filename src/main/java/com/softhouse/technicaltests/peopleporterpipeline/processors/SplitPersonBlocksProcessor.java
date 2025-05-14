package com.softhouse.technicaltests.peopleporterpipeline.processors;

import com.softhouse.technicaltests.peopleporterpipeline.common.LineType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.softhouse.technicaltests.peopleporterpipeline.common.RouteConstants.PROPERTY_EXPECTED_PEOPLE_COUNT;

/**
 * Splits the input text into blocks starting with a 'P|' line.
 */
public class SplitPersonBlocksProcessor implements Processor {

    private static final Pattern LINE_SPLITTER = Pattern.compile("\\R");

    @Override
    public void process(Exchange exchange) {
        String body = exchange.getIn().getBody(String.class);
        String[] lines = LINE_SPLITTER.split(body);  // Updated line

        List<List<String>> personBlocks = new ArrayList<>();
        List<String> currentBlock = new ArrayList<>();

        for (String line : lines) {
            if (isPersonLine(line)) {
                if (!currentBlock.isEmpty()) {
                    personBlocks.add(new ArrayList<>(currentBlock));
                    currentBlock.clear();
                }
            }
            currentBlock.add(line);
        }

        if (!currentBlock.isEmpty()) {
            personBlocks.add(currentBlock);
        }

        // Set number of people as exchange property for later aggregation
        exchange.setProperty(PROPERTY_EXPECTED_PEOPLE_COUNT, personBlocks.size());

        // Join the blocks back into multiline strings
        List<String> joinedBlocks = personBlocks.stream()
                .map(block -> String.join(System.lineSeparator(), block))
                .toList();

        exchange.getIn().setBody(joinedBlocks);
    }

    public static boolean isPersonLine(String line) {
        return line != null && line.startsWith(LineType.PERSON + "|");
    }
}
