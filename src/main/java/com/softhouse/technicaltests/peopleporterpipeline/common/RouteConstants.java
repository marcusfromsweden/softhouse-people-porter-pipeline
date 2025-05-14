package com.softhouse.technicaltests.peopleporterpipeline.common;

public final class RouteConstants {

    // File I/O
    public static final String INPUT_URI = "file:input?noop=true";
    public static final String OUTPUT_URI = "file:output?fileName=people.xml";

    // Route IDs
    public static final String ROUTE_ID_READ_AND_SPLIT_PERSONS = "read-file-and-split-persons";
    public static final String ROUTE_ID_PERSON_STRING_TO_DTOS = "convert-person-block-to-dtos";
    public static final String ROUTE_ID_BUILD_PERSON = "build-person-object";
    public static final String ROUTE_ID_AGGREGATE_PEOPLE = "aggregate-all-people";

    // Direct endpoints
    public static final String ROUTE_PERSON_STRING_TO_DTOS = "direct:person-to-dtos";
    public static final String ROUTE_BUILD_PERSON = "direct:build-person";
    public static final String ROUTE_PEOPLE_AGGREGATOR = "direct:people-aggregator";

    private RouteConstants() {
    }
}
