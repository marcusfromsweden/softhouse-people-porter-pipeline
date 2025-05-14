package com.softhouse.technicaltests.peopleporterpipeline.domain.contract;

import com.softhouse.technicaltests.peopleporterpipeline.domain.Phone;

public interface PhoneHolder {
    Phone getPhone();

    void setPhone(Phone phone);

    default boolean hasPhone() {
        return getPhone() != null;
    }
}
