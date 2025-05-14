package com.softhouse.technicaltests.peopleporterpipeline.routes;

import com.softhouse.technicaltests.peopleporterpipeline.aggregators.PeopleAggregationStrategy;
import com.softhouse.technicaltests.peopleporterpipeline.processors.BuildPersonProcessor;
import com.softhouse.technicaltests.peopleporterpipeline.processors.InputLineParser;
import com.softhouse.technicaltests.peopleporterpipeline.processors.SplitPersonBlocksProcessor;
import org.apache.camel.builder.RouteBuilder;

import static com.softhouse.technicaltests.peopleporterpipeline.common.RouteConstants.*;

public class PeoplePorterRoute extends RouteBuilder {

    @Override
    public void configure() {
        // Route 1: Read file and split into person blocks (strings)
        from(INPUT_URI)
                .routeId(ROUTE_ID_READ_AND_SPLIT_PEOPLE)
                .convertBodyTo(String.class)
                .process(new SplitPersonBlocksProcessor())
                .split(body()).streaming()
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
                .toD(OUTPUT_URI);
    }
}
