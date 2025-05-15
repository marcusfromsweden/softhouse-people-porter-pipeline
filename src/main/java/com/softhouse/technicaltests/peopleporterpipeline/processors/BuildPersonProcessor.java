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

/**
 * A Camel {@link org.apache.camel.Processor} that constructs a {@link com.softhouse.technicaltests.peopleporterpipeline.domain.Person}
 * object from a list of {@link com.softhouse.technicaltests.peopleporterpipeline.input.InputLine} instances.
 *
 * <p>This processor assumes the input is a list of structured lines representing a person and their related data,
 * such as addresses, phone numbers, and family members. The first line must be of type {@code P} (PERSON),
 * and subsequent lines can include {@code A} (ADDRESS), {@code T} (PHONE), and {@code F} (FAMILY_MEMBER).</p>
 *
 * <p>The processor builds a {@code Person} object and embeds addresses, phones, and family members into it.
 * If a phone or address is already present for a given {@link com.softhouse.technicaltests.peopleporterpipeline.domain.contract.PhoneHolder}
 * or {@link com.softhouse.technicaltests.peopleporterpipeline.domain.contract.AddressHolder}, a warning is logged and the duplicate is ignored.</p>
 *
 * <p>Errors such as missing required fields, malformed input lines, or unexpected line ordering will result in a
 * {@link com.softhouse.technicaltests.peopleporterpipeline.exception.BuildPersonProcessorException} being thrown.</p>
 */
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
                    person = handlePersonLine(values);
                    currentFamily = null;
                }
                case ADDRESS -> handleAddressLine(values, inputLine, person, currentFamily);
                case PHONE -> handlePhoneLine(values, inputLine, person, currentFamily);
                case FAMILY_MEMBER -> {
                    currentFamily = handleFamilyLine(values, person);
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

    private Person handlePersonLine(String[] values) {
        if (values.length < 2) {
            throw new BuildPersonProcessorException("PERSON line must contain at least 2 values: first and last name.");
        }
        Person person = new Person();
        person.setFirstname(values[0]);
        person.setLastname(values[1]);
        return person;
    }

    private void handleAddressLine(String[] values, InputLine line, Person person, FamilyMember family) {
        if (values.length < 2) {
            throw new BuildPersonProcessorException("ADDRESS line must contain at least 2 values: street and city.");
        }

        Address address = new Address();
        address.setStreet(values[0]);
        address.setCity(values[1]);
        if (values.length > 2) {
            address.setPostalCode(values[2]);
        }

        if (family != null) {
            setOrWarnAddress(family, address, line);
        } else if (person != null) {
            setOrWarnAddress(person, address, line);
        } else {
            throw new BuildPersonProcessorException("ADDRESS line found without a PERSON or FAMILY_MEMBER line.");
        }
    }

    private void handlePhoneLine(String[] values, InputLine line, Person person, FamilyMember family) {
        if (values.length < 1) {
            throw new BuildPersonProcessorException("PHONE line must contain at least 1 value: mobile number.");
        }

        Phone phone = new Phone();
        phone.setMobile(values[0]);
        if (values.length > 1) {
            phone.setLandLine(values[1]);
        }

        if (family != null) {
            setOrWarnPhone(family, phone, line);
        } else if (person != null) {
            setOrWarnPhone(person, phone, line);
        } else {
            throw new BuildPersonProcessorException("PHONE line found without a PERSON or FAMILY_MEMBER line.");
        }
    }

    private FamilyMember handleFamilyLine(String[] values, Person person) {
        if (person == null) {
            throw new BuildPersonProcessorException("FAMILY_MEMBER line found before PERSON line.");
        }
        if (values.length < 2) {
            throw new BuildPersonProcessorException("FAMILY_MEMBER line must contain at least 2 values: first and birth year.");
        }
        FamilyMember member = new FamilyMember();
        member.setName(values[0]);
        member.setBorn(values[1]);
        return member;
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
