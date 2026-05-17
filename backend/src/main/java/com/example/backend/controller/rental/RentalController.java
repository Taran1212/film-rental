package com.example.backend.controller.rental;

import com.example.backend.dto.RentalConfirmationDto;
import com.example.backend.dto.RentalRequestDto;
import com.example.backend.dto.projection.RentalProjection;
import com.example.backend.service.rental.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;
    @PostMapping
    public RentalConfirmationDto rentMovie(@Valid @RequestBody RentalRequestDto dto) {
        return rentalService.rentMovie(dto);
    }

    @PutMapping("/return/{rentalId}")
    public String returnMovie(@PathVariable Integer rentalId) {
        return rentalService.returnMovie(rentalId);
    }

    @GetMapping("/active")
    public Page<RentalProjection> getActiveRentals(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return rentalService.getActiveRentals(search, PageRequest.of(page, size));
    }

    @GetMapping("/customer/{customerId}")
    public Page<RentalProjection> getCustomerRentals(
            @PathVariable Integer customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return rentalService.getCustomerRentals(customerId, PageRequest.of(page, size));
    }

}
