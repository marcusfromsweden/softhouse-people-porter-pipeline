package com.softhouse.peopleporterpipeline.domain;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a phone record, with mobile and land-line numbers.
 */
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Phone {

    private String mobile;

    @XmlElement(name = "land-line")
    private String landLine;

    @Override
    public String toString() {
        return "Phone[mobile=" + mobile +
                (landLine != null ? ", landLine=" + landLine : "") + "]";
    }
}
