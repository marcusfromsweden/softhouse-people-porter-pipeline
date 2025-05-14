package com.softhouse.technicaltests.peopleporterpipeline.domain;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Root XML element that wraps a list of Person entries.
 * Used as the output object for JAXB marshalling.
 */
@Data
@NoArgsConstructor
@XmlRootElement(name = "people")
@XmlAccessorType(XmlAccessType.FIELD)
public class People {

    @XmlElement(name = "person")
    private final List<Person> people = new ArrayList<>();
}
