package com.softhouse.technicaltests.peopleporterpipeline.domain;

import com.softhouse.technicaltests.peopleporterpipeline.domain.contract.AddressHolder;
import com.softhouse.technicaltests.peopleporterpipeline.domain.contract.PhoneHolder;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a top-level person entity in the People Porter pipeline.
 * <p>
 * Each person may contain:
 * <ul>
 *   <li>First and last name</li>
 *   <li>A single {@link Address}</li>
 *   <li>A single {@link Phone}</li>
 *   <li>A list of {@link FamilyMember} instances</li>
 * </ul>
 * <p>
 * Implements {@link PhoneHolder} and {@link AddressHolder} to support uniform handling
 * of phone and address information in both persons and family members.
 *
 * @see Address
 * @see Phone
 * @see FamilyMember
 */
@Data
@NoArgsConstructor
@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements PhoneHolder, AddressHolder {

    private String firstname;
    private String lastname;
    private Address address;
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
