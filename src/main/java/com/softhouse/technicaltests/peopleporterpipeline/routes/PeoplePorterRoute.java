package com.softhouse.technicaltests.peopleporterpipeline.routes;

import com.softhouse.technicaltests.peopleporterpipeline.aggregators.PeopleAggregationStrategy;
import com.softhouse.technicaltests.peopleporterpipeline.processors.BuildPersonProcessor;
import com.softhouse.technicaltests.peopleporterpipeline.processors.InputLineParser;
import com.softhouse.technicaltests.peopleporterpipeline.processors.SplitPersonBlocksProcessor;
import org.apache.camel.builder.RouteBuilder;

import static com.softhouse.technicaltests.peopleporterpipeline.common.RouteConstants.*;

/**
 * Defines the main Apache Camel routing logic for the PeoplePorterPipeline application.
 * <p>
 * This route processes flat text files representing people and their related data
 * (addresses, phones, family members), transforms them into domain objects, and outputs
 * a structured XML file using JAXB marshalling.
 * <p>
 * The pipeline is composed of four main stages:
 * <ol>
 *     <li><b>File Ingestion & Splitting</b>: Reads the input file and splits it into blocks per person.</li>
 *     <li><b>Parsing</b>: Each block is parsed into structured {@code InputLine} objects.</li>
 *     <li><b>Transformation</b>: InputLines are converted into fully populated {@code Person} domain objects.</li>
 *     <li><b>Aggregation</b>: All Person objects are aggregated into a {@code People} container and marshalled to XML.</li>
 * </ol>
 * <p>
 * This class provides both a default constructor and a customizable constructor for injecting different URIs — useful in integration tests.
 * <p>
 * Errors during processing are caught by a global {@code onException} handler and routed to a dead-letter endpoint.
 *
 * @see org.apache.camel.builder.RouteBuilder
 * @see com.softhouse.technicaltests.peopleporterpipeline.domain.Person
 * @see com.softhouse.technicaltests.peopleporterpipeline.domain.People
 * @see com.softhouse.technicaltests.peopleporterpipeline.processors.BuildPersonProcessor
 * @see com.softhouse.technicaltests.peopleporterpipeline.aggregators.PeopleAggregationStrategy
 */
public class PeoplePorterRoute extends RouteBuilder {

    private final String inputUri;
    private final String outputUri;
    private final String errorUri;

    // Constructor using default route URIs
    public PeoplePorterRoute() {
        this(INPUT_URI, OUTPUT_URI, ERROR_FOLDER_URI);
    }

    // Constructor allowing custom route URIs (e.g. for testing)
    public PeoplePorterRoute(String inputUri, String outputUri, String errorUri) {
        this.inputUri = inputUri;
        this.outputUri = outputUri;
        this.errorUri = errorUri;
    }

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .useOriginalMessage()
                .to(errorUri)
                .log("Error processing file: ${header.CamelFileName} - ${exception.message}");

        // Route 1: Read file and split into person blocks (strings)
        from(inputUri)
                .routeId(ROUTE_ID_READ_AND_SPLIT_PEOPLE)
                .convertBodyTo(String.class)
                .process(new SplitPersonBlocksProcessor())
                .split(body()).streaming().shareUnitOfWork().stopOnException()
                .log("Splitting person blocks: ${body}")
                .to(ROUTE_PERSON_STRING_TO_INPUT_LINES);

        // Route 2: Convert each person block string -> InputLines -> Person
        from(ROUTE_PERSON_STRING_TO_INPUT_LINES)
                .routeId(ROUTE_ID_PERSON_STRING_TO_INPUT_LINES)
                .process(new InputLineParser())
                .log("Converting person block to InputLines: ${body}")
                .to(ROUTE_BUILD_PERSON);

        // Route 3: Build the Person object
        from(ROUTE_BUILD_PERSON)
                .routeId(ROUTE_ID_BUILD_PERSON)
                .process(new BuildPersonProcessor())
                .log("Building Person object: ${body}")
                .to(ROUTE_PEOPLE_AGGREGATOR);

        // Route 4: Aggregate all persons into one People object and marshal to XML
        from(ROUTE_PEOPLE_AGGREGATOR)
                .routeId(ROUTE_ID_AGGREGATE_PEOPLE)
                .aggregate(constant(true), new PeopleAggregationStrategy())
                .completionSize(exchangeProperty(PROPERTY_EXPECTED_PEOPLE_COUNT))
                .log("Marshalling People with ${body.people.size()} persons: ${body}")
                .marshal().jaxb()
                .toD(outputUri);
    }
}
