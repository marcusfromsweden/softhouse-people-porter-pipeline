package com.softhouse.technicaltests.peopleporterpipeline.aggregators;

import com.softhouse.technicaltests.peopleporterpipeline.domain.People;
import com.softhouse.technicaltests.peopleporterpipeline.domain.Person;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregates {@link Person} objects into a single {@link People} container object.
 * <p>
 * This strategy is used in the final step of the Camel pipeline to collect all
 * processed Person instances into one {@code People} object, which is then serialized to XML.
 * <p>
 * The aggregation operates on the assumption that each incoming {@link Exchange}
 * contains one {@link Person}. The first person initializes the {@code People} object,
 * and subsequent ones are appended to it.
 * <p>
 * Camel's {@code aggregate()} method is invoked iteratively during message aggregation.
 * This strategy is used in combination with a {@code constant(true)} correlation key,
 * which forces all exchanges to be aggregated together in sequence.
 *
 * @see People
 * @see Person
 * @see org.apache.camel.processor.aggregate.AggregateProcessor
 */
public class PeopleAggregationStrategy implements AggregationStrategy {

    private static final Logger log = LoggerFactory.getLogger(PeopleAggregationStrategy.class);

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Person newPerson = newExchange.getIn().getBody(Person.class);

        People people;
        if (oldExchange == null) {
            // First aggregation
            List<Person> personList = new ArrayList<>();
            personList.add(newPerson);
            people = new People(personList);
            
            log.debug("Started People aggregation with: {}", newPerson);

            newExchange.getIn().setBody(people);
            return newExchange;
        }

        // Continuing aggregation
        people = oldExchange.getIn().getBody(People.class);
        people.getPeople().add(newPerson);

        log.debug("Added Person to People aggregation: {}", newPerson);

        oldExchange.getIn().setBody(people);
        return oldExchange;
    }
}
