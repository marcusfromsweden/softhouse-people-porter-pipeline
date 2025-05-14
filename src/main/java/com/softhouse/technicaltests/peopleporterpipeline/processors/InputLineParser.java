package com.softhouse.technicaltests.peopleporterpipeline.processors;

import com.softhouse.technicaltests.peopleporterpipeline.input.InputLine;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Converts a block of lines into List<InputLine>.
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
                String[] values = Arrays.copyOfRange(parts, 1, parts.length);
                inputLines.add(new InputLine(lineType, values, line));
            }
        }

        exchange.getIn().setBody(inputLines);
    }
}
