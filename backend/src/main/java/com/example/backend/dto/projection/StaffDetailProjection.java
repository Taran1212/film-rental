package com.example.backend.dto.projection;

public interface StaffDetailProjection {

    Integer getStaffId();

    String getFullName();

    String getUsername();

    String getEmail();

    Integer getStoreId();

    Boolean getActive();

    String getAddress();

    String getAddress2();

    String getDistrict();

    String getCity();

    String getCountry();

    String getPostalCode();

    String getPhone();
}