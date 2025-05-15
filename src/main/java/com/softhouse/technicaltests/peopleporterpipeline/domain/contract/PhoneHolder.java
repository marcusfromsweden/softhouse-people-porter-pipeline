package com.softhouse.technicaltests.peopleporterpipeline.domain.contract;

import com.softhouse.technicaltests.peopleporterpipeline.domain.Phone;

/**
 * Interface for entities capable of holding a {@link Phone} object.
 */
//todo rewrite java doc
public interface PhoneHolder {
    Phone getPhone();

    void setPhone(Phone phone);

    default boolean hasPhone() {
        return getPhone() != null;
    }
}
