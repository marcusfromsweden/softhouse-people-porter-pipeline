package com.softhouse.technicaltests.peopleporterpipeline.aggregators;

import com.softhouse.technicaltests.peopleporterpipeline.domain.People;
import com.softhouse.technicaltests.peopleporterpipeline.domain.Person;
import com.softhouse.technicaltests.peopleporterpipeline.exception.PeopleAggregationStrategyException;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aggregation strategy that combines multiple {@link Person} objects into a single {@link People} container.
 * <p>
 * This class is used by Apache Camel's {@code aggregate()} DSL to collect individual {@code Person}
 * instances into one {@code People} object, which will later be marshalled to XML.
 * <p>
 * The strategy assumes each incoming {@link Exchange} contains a single {@code Person} object.
 * It handles two cases:
 * <ul>
 *     <li>When {@code oldExchange} is {@code null} (i.e., the first exchange in the group), a new
 *     {@code People} instance is created, and the {@code Person} from {@code newExchange} is added to it.</li>
 *     <li>Subsequent exchanges add their {@code Person} to the existing {@code People} object from {@code oldExchange}.</li>
 * </ul>
 * <p>
 * A {@link PeopleAggregationStrategyException} is thrown if the {@code newExchange} does not contain a valid {@code Person}.
 *
 * <p><strong>Logging:</strong> Debug-level logs provide insight into when aggregation starts and when new people are added.
 *
 * @see org.apache.camel.AggregationStrategy
 * @see com.softhouse.technicaltests.peopleporterpipeline.domain.People
 * @see com.softhouse.technicaltests.peopleporterpipeline.domain.Person
 * @see com.softhouse.technicaltests.peopleporterpipeline.exception.PeopleAggregationStrategyException
 */
public class PeopleAggregationStrategy implements AggregationStrategy {

    private static final Logger log = LoggerFactory.getLogger(PeopleAggregationStrategy.class);

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Person newPerson = newExchange.getIn().getBody(Person.class);

        if (newPerson == null) {
            throw new PeopleAggregationStrategyException("New Person is null. Cannot aggregate.");
        }

        People people;
        if (oldExchange == null) {
            // First aggregation
            people = new People();
            people.getPeople().add(newPerson);

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
