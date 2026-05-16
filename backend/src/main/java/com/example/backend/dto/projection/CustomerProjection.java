package com.example.backend.dto.projection;

import org.springframework.beans.factory.annotation.Value;

public interface CustomerProjection {

    Integer getCustomerId();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();

    String getEmail();

    Integer getStoreId();

    Boolean getActive();
}
