package com.softhouse.technicaltests.peopleporterpipeline.domain;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an address.
 * Postal code is optional.
 */
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Address {

    private String street;
    private String city;

    @XmlElement(name = "postal-code")
    private String postalCode;

    @Override
    public String toString() {
        return "Address[street=" + street +
                ", city=" + city +
                (postalCode != null ? ", postalCode=" + postalCode : "") + "]";
    }
}
