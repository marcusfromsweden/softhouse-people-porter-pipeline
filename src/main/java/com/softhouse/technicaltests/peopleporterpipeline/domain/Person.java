package com.softhouse.technicaltests.peopleporterpipeline.domain;

import com.softhouse.technicaltests.peopleporterpipeline.domain.contract.AddressHolder;
import com.softhouse.technicaltests.peopleporterpipeline.domain.contract.PhoneHolder;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a top-level person entity with name, addresses, phones, and family members.
 */
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements PhoneHolder, AddressHolder {

    private String firstname;
    private String lastname;

    @XmlElement(name = "address")
    private Address address;

    @XmlElement(name = "phone")
    private Phone phone;

    @XmlElement(name = "family")
    private final List<FamilyMember> familyMembers = new ArrayList<>();

    @Override
    public String toString() {
        return "Person[first=" + firstname +
                ", last=" + lastname +
                ", address=" + (address != null ? address : "") +
                ", phone=" + (phone != null ? phone : "") +
                ", familyMembers=" + (!familyMembers.isEmpty() ? familyMembers : "") + "]";
    }
}
