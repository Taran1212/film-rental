package com.example.backend.dto.projection;

import org.springframework.beans.factory.annotation.Value;

public interface StaffDetailProjection {

    Integer getStaffId();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();

    String getUsername();

    String getEmail();

    Integer getStoreId();

    Boolean getActive();

    @Value("#{target.address?.address}")
    String getAddress();

    @Value("#{target.address?.address2}")
    String getAddress2();

    @Value("#{target.address?.district}")
    String getDistrict();

    @Value("#{target.address?.city?.city}")
    String getCity();

    @Value("#{target.address?.city?.country?.country}")
    String getCountry();

    @Value("#{target.address?.postalCode}")
    String getPostalCode();

    @Value("#{target.address?.phone}")
    String getPhone();
}
