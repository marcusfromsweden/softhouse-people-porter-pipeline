package com.softhouse.technicaltests.peopleporterpipeline.processors;

import com.softhouse.technicaltests.peopleporterpipeline.common.LineType;
import com.softhouse.technicaltests.peopleporterpipeline.exception.InputLineParserException;
import com.softhouse.technicaltests.peopleporterpipeline.input.InputLine;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A Camel {@link org.apache.camel.Processor} responsible for parsing a block of input lines (as a single {@link String})
 * into a list of structured {@link com.softhouse.technicaltests.peopleporterpipeline.input.InputLine} objects.
 *
 * <p>This processor is typically used after the input file has been split into person-related text blocks.
 * Each block represents one person and their related data (e.g., phone, address, family member lines).</p>
 *
 * <p>Each line is split using {@code \R} (any line separator) and then parsed using {@code |} as a field separator.
 * The first segment of each line is interpreted as a line type, which must match one of the valid types defined in {@link com.softhouse.technicaltests.peopleporterpipeline.common.LineType}:
 * <ul>
 *     <li>{@code P} - Person</li>
 *     <li>{@code T} - Phone</li>
 *     <li>{@code A} - Address</li>
 *     <li>{@code F} - Family Member</li>
 * </ul>
 * </p>
 *
 * <p>If an unknown or unsupported line type is encountered, an {@link com.softhouse.technicaltests.peopleporterpipeline.exception.InputLineParserException}
 * is thrown, terminating the route execution unless handled upstream.</p>
 */

public class InputLineParser implements Processor {

    private static final Pattern LINE_SPLITTER = Pattern.compile("\\R");
    private static final Pattern FIELD_SPLITTER = Pattern.compile("\\|");

    @Override
    public void process(Exchange exchange) {
        String lines = exchange.getIn().getBody(String.class);
        List<InputLine> inputLines = new ArrayList<>();

        for (String line : LINE_SPLITTER.split(lines)) {
            line = line.trim();
            if (!line.isEmpty()) {
                String[] parts = FIELD_SPLITTER.split(line);
                String lineType = parts[0];

                if (!LineType.isValid(lineType)) {
                    throw new InputLineParserException("Unsupported line type: '%s' in line: %s".formatted(lineType, line));
                }

                // Validate number of parts (must be more than just the line type)
                if (parts.length < 2 || (parts.length == 2 && parts[1].isBlank())) {
                    throw new InputLineParserException(
                            "Line type '%s' is missing expected values in line: %s".formatted(lineType, line)
                    );
                }
                
                String[] values = Arrays.copyOfRange(parts, 1, parts.length);
                inputLines.add(new InputLine(lineType, values, line));
            }
        }

        exchange.getIn().setBody(inputLines);
    }
}
