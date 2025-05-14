package com.softhouse.technicaltests.peopleporterpipeline.domain;

import com.softhouse.technicaltests.peopleporterpipeline.domain.contract.AddressHolder;
import com.softhouse.technicaltests.peopleporterpipeline.domain.contract.PhoneHolder;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a family member of a person.
 * Internally called FamilyMember, but serialized as <family> in XML.
 */
@Data
@NoArgsConstructor
@XmlRootElement(name = "family")
@XmlAccessorType(XmlAccessType.FIELD)
public class FamilyMember implements PhoneHolder, AddressHolder {

    private String name;
    private String born;

    @XmlElement(name = "address")
    private Address address;

    @XmlElement(name = "phone")
    private Phone phone;

    @Override
    public String toString() {
        return "FamilyMember[name=" + name +
                ", born=" + born +
                ", addresses=" + (address != null ? address : "") +
                ", phones=" + (phone != null ? phone : "") + "]";
    }
}
