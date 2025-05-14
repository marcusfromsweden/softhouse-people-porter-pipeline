package com.softhouse.technicaltests.peopleporterpipeline.domain.contract;

import com.softhouse.technicaltests.peopleporterpipeline.domain.Address;

public interface AddressHolder {
    Address getAddress();

    void setAddress(Address address);

    default boolean hasAddress() {
        return getAddress() != null;
    }
}