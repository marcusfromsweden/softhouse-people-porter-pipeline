package com.softhouse.peopleporterpipeline.input;

/**
 * Represents a parsed line from the input file.
 * Holds the line type (e.g., "P", "T", "A", "F") and its values.
 */
public record InputLine(String type, String[] values) {
    @Override
    public String toString() {
        return "InputLine[type=%s, values=%s]".formatted(
                type,
                String.join("|", values)
        );
    }
}
