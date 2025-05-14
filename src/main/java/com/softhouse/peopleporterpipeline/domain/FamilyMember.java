package com.softhouse.peopleporterpipeline.domain;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a family member of a person.
 * Internally called FamilyMember, but serialized as <family> in XML.
 */
@Data
@NoArgsConstructor
@XmlRootElement(name = "family")
@XmlAccessorType(XmlAccessType.FIELD)
public class FamilyMember {

    private String name;
    private String born;

    @XmlElement(name = "address")
    private List<Address> addresses;

    @XmlElement(name = "phone")
    private List<Phone> phones;

    @Override
    public String toString() {
        return "FamilyMember[name=" + name +
                ", born=" + born +
                ", addresses=" + (addresses != null ? addresses.size() : 0) +
                ", phones=" + (phones != null ? phones.size() : 0) + "]";
    }
}
