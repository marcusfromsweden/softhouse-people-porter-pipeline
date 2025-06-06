package com.softhouse.technicaltests.peopleporterpipeline.common;

/**
 * Centralized definition of route URIs, route IDs, direct endpoint names,
 * and the exchange property {@link #PROPERTY_EXPECTED_PEOPLE_COUNT}
 * that controls aggregation completion.
 */
public final class RouteConstants {

    // File I/O
    public static final String INPUT_URI = "file:camel/input?include=.*\\.txt&move=../processed";
    public static final String OUTPUT_URI = "file:camel/output?fileName=${file:name.noext}.xml";
    public static final String ERROR_FOLDER_URI = "file:camel/error";

    // Route IDs
    public static final String ROUTE_ID_READ_AND_SPLIT_PEOPLE = "read-file-and-split-people";
    public static final String ROUTE_ID_PERSON_STRING_TO_INPUT_LINES = "convert-person-block-to-input-lines";
    public static final String ROUTE_ID_BUILD_PERSON = "build-person-object";
    public static final String ROUTE_ID_AGGREGATE_PEOPLE = "aggregate-all-people";

    // Direct endpoints
    public static final String ROUTE_PERSON_STRING_TO_INPUT_LINES = "direct:person-to-input-lines";
    public static final String ROUTE_BUILD_PERSON = "direct:build-person";
    public static final String ROUTE_PEOPLE_AGGREGATOR = "direct:people-aggregator";

    // Properties
    public static final String PROPERTY_EXPECTED_PEOPLE_COUNT = "expectedPersonCount";

    private RouteConstants() {
    }
}
