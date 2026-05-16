package com.example.backend.dto.projection;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface RentalProjection {
    Integer getRentalId();

    @Value("#{target.inventory.film.title}")
    String getMovieTitle();

    @Value("#{target.customer.firstName + ' ' + target.customer.lastName}")
    String getCustomerName();

    LocalDateTime getRentalDate();

    LocalDateTime getReturnDate();

    @Value("#{target.returnDate != null}")
    Boolean getReturned();
}
