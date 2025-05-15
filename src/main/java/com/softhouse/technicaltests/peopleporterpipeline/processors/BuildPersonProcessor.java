package com.softhouse.technicaltests.peopleporterpipeline.processors;

import com.softhouse.technicaltests.peopleporterpipeline.domain.Address;
import com.softhouse.technicaltests.peopleporterpipeline.domain.FamilyMember;
import com.softhouse.technicaltests.peopleporterpipeline.domain.Person;
import com.softhouse.technicaltests.peopleporterpipeline.domain.Phone;
import com.softhouse.technicaltests.peopleporterpipeline.domain.contract.AddressHolder;
import com.softhouse.technicaltests.peopleporterpipeline.domain.contract.PhoneHolder;
import com.softhouse.technicaltests.peopleporterpipeline.exception.BuildPersonProcessorException;
import com.softhouse.technicaltests.peopleporterpipeline.input.InputLine;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.softhouse.technicaltests.peopleporterpipeline.common.LineType.*;

public class BuildPersonProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(BuildPersonProcessor.class);

    @Override
    public void process(Exchange exchange) {
        @SuppressWarnings("unchecked")
        List<InputLine> lines = exchange.getMessage().getBody(List.class);

        if (lines == null || lines.isEmpty()) {
            throw new BuildPersonProcessorException("No lines provided to build a Person.");
        }

        if (!PERSON.equals(lines.getFirst().type())) {
            throw new BuildPersonProcessorException("First line must be of type '" + PERSON + "', found: " + lines.getFirst().type());
        }

        log.info("Building Person: block size={}. InputLines: {}", lines.size(), lines);

        Person person = null;
        FamilyMember currentFamily = null;

        for (InputLine inputLine : lines) {
            if (inputLine == null || inputLine.values() == null) {
                log.warn("Skipping null or malformed InputLine: {}", inputLine);
                continue;
            }

            String[] values = inputLine.values();

            switch (inputLine.type()) {
                case PERSON -> {
                    if (values.length < 2) {
                        throw new BuildPersonProcessorException("PERSON line must contain at least 2 values: first and last name.");
                    }
                    person = new Person();
                    person.setFirstname(values[0]);
                    person.setLastname(values[1]);
                    currentFamily = null;
                }

                case ADDRESS -> {
                    Address address = new Address();
                    if (values.length < 2) {
                        throw new BuildPersonProcessorException("ADDRESS line must contain at least 2 values: street and city.");
                    }
                    address.setStreet(values[0]);
                    address.setCity(values[1]);
                    if (values.length > 2) {
                        address.setPostalCode(values[2]);
                    }

                    if (currentFamily != null) {
                        setOrWarnAddress(currentFamily, address, inputLine);
                    } else if (person != null) {
                        setOrWarnAddress(person, address, inputLine);
                    } else {
                        throw new BuildPersonProcessorException("ADDRESS line found without a PERSON or FAMILY_MEMBER line.");
                    }
                }

                case PHONE -> {
                    Phone phone = new Phone();
                    if (values.length < 1) {
                        throw new BuildPersonProcessorException("PHONE line must contain at least 1 value: mobile number.");
                    }
                    phone.setMobile(values[0]);
                    if (values.length > 1) {
                        phone.setLandLine(values[1]);
                    }

                    if (currentFamily != null) {
                        setOrWarnPhone(currentFamily, phone, inputLine);
                    } else if (person != null) {
                        setOrWarnPhone(person, phone, inputLine);
                    } else {
                        throw new BuildPersonProcessorException("PHONE line found without a PERSON or FAMILY_MEMBER line.");
                    }
                }

                case FAMILY_MEMBER -> {
                    if (person == null) {
                        throw new BuildPersonProcessorException("FAMILY_MEMBER line found before PERSON line.");
                    }
                    if (values.length < 2) {
                        throw new BuildPersonProcessorException("FAMILY_MEMBER line must contain at least 2 values: first and last name.");
                    }

                    currentFamily = new FamilyMember();
                    currentFamily.setName(values[0]);
                    currentFamily.setBorn(values[1]);
                    person.getFamilyMembers().add(currentFamily);
                }

                default -> throw new BuildPersonProcessorException("Unexpected line type: " + inputLine.type());
            }
        }

        if (person == null) {
            throw new BuildPersonProcessorException("Person block missing a '" + PERSON + "' line.");
        }

        log.debug("Built Person: {}", person);
        exchange.getMessage().setBody(person);
    }

    private void setOrWarnPhone(PhoneHolder holder, Phone phone, InputLine inputLine) {
        if (holder.hasPhone()) {
            log.warn("Multiple phone entries detected. Ignoring additional input line: {}", inputLine.rawLine());
        } else {
            holder.setPhone(phone);
        }
    }

    private void setOrWarnAddress(AddressHolder holder, Address address, InputLine inputLine) {
        if (holder.hasAddress()) {
            log.warn("Multiple address entries detected. Ignoring additional input line: {}", inputLine.rawLine());
        } else {
            holder.setAddress(address);
        }
    }

}
