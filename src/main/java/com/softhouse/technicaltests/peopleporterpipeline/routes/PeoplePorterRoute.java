package com.softhouse.technicaltests.peopleporterpipeline.routes;

import com.softhouse.technicaltests.peopleporterpipeline.processors.InputLineParser;
import com.softhouse.technicaltests.peopleporterpipeline.processors.SplitPersonBlocksProcessor;
import org.apache.camel.builder.RouteBuilder;

import static com.softhouse.technicaltests.peopleporterpipeline.common.RouteConstants.*;

public class PeoplePorterRoute extends RouteBuilder {

    @Override
    public void configure() {
        // Route 1: Read file and split into person blocks (strings)
        from(INPUT_URI)
                .routeId(ROUTE_ID_READ_AND_SPLIT_PERSONS)
                .convertBodyTo(String.class)
                .process(new SplitPersonBlocksProcessor())
                .split(body()).streaming()
                .log("Splitting person blocks: ${body}")
                .to(ROUTE_PERSON_STRING_TO_INPUT_LINES);

        // Route 2: Convert each person block string -> InputLines -> Person
        from(ROUTE_PERSON_STRING_TO_INPUT_LINES)
                .routeId(ROUTE_ID_PERSON_STRING_TO_INPUT_LINES)
                .process(new InputLineParser())
                .log("Converting person block to InputLines: ${body}");
    }
}
