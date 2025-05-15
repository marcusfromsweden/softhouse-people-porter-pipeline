package com.softhouse.technicaltests.peopleporterpipeline.input;

/**
 * Represents a single parsed line from the input text file used in the People Porter pipeline.
 * <p>
 * Each line in the input file is expected to be of the form {@code TYPE|value1|value2|...}.
 * <ul>
 *     <li>{@code type} – A single-character string indicating the type of line (e.g. {@code P}, {@code T}, {@code A}, {@code F}).</li>
 *     <li>{@code values} – An array of values extracted from the line, following the line type.</li>
 *     <li>{@code rawLine} – The original unmodified string from the input file, useful for logging and diagnostics.</li>
 * </ul>
 */
public record InputLine(String type, String[] values, String rawLine) {
    @Override
    public String toString() {
        return "InputLine[type=%s, values=%s, raw: %s]".formatted(
                type,
                String.join("|", values),
                rawLine
        );
    }
}
