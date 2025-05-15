package com.softhouse.technicaltests.peopleporterpipeline.processors;

import com.softhouse.technicaltests.peopleporterpipeline.common.LineType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.softhouse.technicaltests.peopleporterpipeline.common.RouteConstants.PROPERTY_EXPECTED_PEOPLE_COUNT;

/**
 * A Camel {@link org.apache.camel.Processor} that splits a flat text input into logical blocks,
 * where each block starts with a {@code P|} (Person) line.
 *
 * <p>This processor is used to segment a multi-line input (such as a full file) into individual person
 * blocks, which are then processed further downstream. A block includes all lines belonging to a single
 * person, including related data like addresses, phones, and family members.</p>
 *
 * <p>Each block is represented as a multi-line string. The result is a list of such strings,
 * one per person. The processor also sets a property on the exchange to indicate how many person
 * blocks were identified. This property is used later for aggregation logic.</p>
 *
 * @see com.softhouse.technicaltests.peopleporterpipeline.common.LineType
 * @see com.softhouse.technicaltests.peopleporterpipeline.common.RouteConstants#PROPERTY_EXPECTED_PEOPLE_COUNT
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
