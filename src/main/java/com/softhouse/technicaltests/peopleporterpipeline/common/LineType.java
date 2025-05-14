package com.softhouse.technicaltests.peopleporterpipeline.common;

import java.util.Set;

/**
 * Defines constants for supported input line types
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
