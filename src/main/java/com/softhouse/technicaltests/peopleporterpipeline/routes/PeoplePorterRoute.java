package com.softhouse.technicaltests.peopleporterpipeline.routes;

import com.softhouse.technicaltests.peopleporterpipeline.processors.*;
import org.apache.camel.builder.RouteBuilder;

import java.io.InputStream;

import static com.softhouse.technicaltests.peopleporterpipeline.common.RouteConstants.*;

/**
 * Defines the main Apache Camel routing logic for the People Porter Pipeline application.
 * <p>
 * This route ingests structured text files from a directory, processes each block of lines representing
 * a person, converts them to domain objects, and writes them as XML using streaming processing.
 * The solution is optimized for handling large files efficiently by avoiding aggregation and using StAX (Streaming API for XML).
 *
 * <h2>Core Responsibilities</h2>
 * <ul>
 *   <li>Read input files from disk via Camel's {@code file:} component.</li>
 *   <li>Stream the file contents line by line using {@link InputStream} to avoid loading the entire file into memory.</li>
 *   <li>Split the file into person blocks using {@link SplitPersonBlocksProcessor}.</li>
 *   <li>Process each person block in parallel with {@link InputLineParser}, {@link BuildPersonProcessor}, and {@link StreamingXmlWriterProcessor}.</li>
 *   <li>Write the final closing {@code </people>} tag using {@link StreamingXmlFooterProcessor} once all person blocks are written.</li>
 * </ul>
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

        from(inputUri)
                .routeId(ROUTE_ID_READ_AND_SPLIT_PEOPLE)
                .convertBodyTo(InputStream.class)
                .process(new SplitPersonBlocksProcessor())
                .split(body()).streaming().parallelProcessing().stopOnException().shareUnitOfWork()
                .process(new InputLineParser())
                .process(new BuildPersonProcessor())
                .process(new StreamingXmlWriterProcessor())
                .end()
                .onCompletion()
                .modeBeforeConsumer()
                .process(new StreamingXmlFooterProcessor())
                .end();
    }
}
