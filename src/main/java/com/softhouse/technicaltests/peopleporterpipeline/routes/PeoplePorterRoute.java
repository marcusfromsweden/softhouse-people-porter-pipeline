package com.softhouse.technicaltests.peopleporterpipeline.routes;

import com.softhouse.technicaltests.peopleporterpipeline.processors.SplitPersonBlocksProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.softhouse.technicaltests.peopleporterpipeline.common.RouteConstants.*;

public class PeoplePorterRoute extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(PeoplePorterRoute.class);

    @Override
    public void configure() {
        // Route A: Read file and split into person blocks (strings)
        from(INPUT_URI)
                .routeId(ROUTE_ID_READ_AND_SPLIT_PERSONS)
                .convertBodyTo(String.class)
                .process(new SplitPersonBlocksProcessor())
                .split(body()).streaming()
                .log("📦 Splitting person blocks: ${body}");
    }
}
