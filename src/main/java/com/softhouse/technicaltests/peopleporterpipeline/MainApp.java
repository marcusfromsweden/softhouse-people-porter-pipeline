package com.softhouse.technicaltests.peopleporterpipeline;

import com.softhouse.technicaltests.peopleporterpipeline.routes.PeoplePorterRoute;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * Entry point for the PeoplePorterPipeline application.
 * Initializes the Camel context and loads the main route.
 */
public class MainApp {

    public static void main(String[] args) throws Exception {
        try (CamelContext context = new DefaultCamelContext()) {
            context.addRoutes(new PeoplePorterRoute());
            context.start();
            Thread.currentThread().join();
        }
    }
}
