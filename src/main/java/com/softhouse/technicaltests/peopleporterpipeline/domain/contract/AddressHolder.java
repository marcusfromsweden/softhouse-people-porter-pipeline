package com.softhouse.technicaltests.peopleporterpipeline.domain.contract;

import com.softhouse.technicaltests.peopleporterpipeline.domain.Address;

/**
 * Interface for entities capable of holding a {@link Address} object.
 */
//todo rewrite java doc
public interface AddressHolder {
    Address getAddress();

    void setAddress(Address address);

    default boolean hasAddress() {
        return getAddress() != null;
    }
}