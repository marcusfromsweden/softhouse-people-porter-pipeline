package com.softhouse.peopleporterpipeline.domain;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a top-level person entity with name, addresses, phones, and family members.
 */
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Person {

    private String firstname;
    private String lastname;

    @XmlElement(name = "address")
    private List<Address> addresses;

    @XmlElement(name = "phone")
    private List<Phone> phones;

    @XmlElement(name = "family")
    private List<FamilyMember> familyMembers;

    @Override
    public String toString() {
        return "Person[first=" + firstname +
                ", last=" + lastname +
                ", addresses=" + (addresses != null ? addresses.size() : 0) +
                ", phones=" + (phones != null ? phones.size() : 0) +
                ", family=" + (familyMembers != null ? familyMembers.size() : 0) + "]";
    }
}
