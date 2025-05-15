package com.softhouse.technicaltests.peopleporterpipeline.common;

import java.util.Set;

/**
 * Defines the valid line type codes used to parse structured person input files.
 * <p>
 * These constants represent the prefixes for lines describing:
 * <ul>
 *   <li>{@code P} - Person</li>
 *   <li>{@code T} - Phone</li>
 *   <li>{@code A} - Address</li>
 *   <li>{@code F} - Family Member</li>
 * </ul>
 */

public final class LineType {

    public static final String PERSON = "P";
    public static final String PHONE = "T";
    public static final String ADDRESS = "A";
    public static final String FAMILY_MEMBER = "F";

    private static final Set<String> VALID_TYPES = Set.of(PERSON, PHONE, ADDRESS, FAMILY_MEMBER);

    public static boolean isValid(String type) {
        return VALID_TYPES.contains(type);
    }

    private LineType() {
    }
}
