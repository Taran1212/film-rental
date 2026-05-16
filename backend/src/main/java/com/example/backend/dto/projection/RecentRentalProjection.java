package com.example.backend.dto.projection;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

// Dashboard widget — same join shape as RentalProjection minus returnDate.
public interface RecentRentalProjection {

    Integer getRentalId();

    @Value("#{target.inventory.film.title}")
    String getMovieTitle();

    @Value("#{target.customer.firstName + ' ' + target.customer.lastName}")
    String getCustomerName();

    LocalDateTime getRentalDate();

    @Value("#{target.returnDate != null}")
    Boolean getReturned();
}
