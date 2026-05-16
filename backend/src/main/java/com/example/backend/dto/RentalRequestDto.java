package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalRequestDto {

    @NotNull(message = "Inventory ID is required")
    @Positive(message = "Inventory ID must be positive")
    private Integer inventoryId;

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be positive")
    private Integer customerId;

    @NotNull(message = "Staff ID is required")
    @Positive(message = "Staff ID must be positive")
    private Integer staffId;
}
