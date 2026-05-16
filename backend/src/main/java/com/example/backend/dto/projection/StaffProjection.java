package com.example.backend.dto.projection;

import org.springframework.beans.factory.annotation.Value;

public interface StaffProjection {

    Integer getStaffId();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();

    String getEmail();

    Boolean getActive();
}
