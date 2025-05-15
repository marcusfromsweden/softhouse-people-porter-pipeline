package com.softhouse.technicaltests.peopleporterpipeline.domain.contract;

import com.softhouse.technicaltests.peopleporterpipeline.domain.Address;

/**
 * Interface for entities that include a {@link Address} property.
 */
public interface AddressHolder {
    Address getAddress();

    void setAddress(Address address);

    default boolean hasAddress() {
        return getAddress() != null;
    }
}