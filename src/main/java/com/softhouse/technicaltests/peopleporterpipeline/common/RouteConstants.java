package com.softhouse.technicaltests.peopleporterpipeline.common;

public final class RouteConstants {

    // File I/O
    public static final String INPUT_URI = "file:input?noop=true";

    // Route IDs
    public static final String ROUTE_ID_READ_AND_SPLIT_PERSONS = "read-file-and-split-persons";
    public static final String ROUTE_ID_PERSON_STRING_TO_INPUT_LINES = "convert-person-block-to-input-lines";

    // Direct endpoints
    public static final String ROUTE_PERSON_STRING_TO_INPUT_LINES = "direct:person-to-dtos";

    private RouteConstants() {
    }
}
