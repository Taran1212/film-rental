package com.example.backend.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class RentalConfirmationDto {

    private Integer rentalId;

    private String movieTitle;

    private String customerName;

    private Integer copyId;

    private BigDecimal amount;
}
