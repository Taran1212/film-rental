package com.example.backend.controller.rental;

import com.example.backend.dto.RentalConfirmationDto;
import com.example.backend.dto.RentalRequestDto;
import com.example.backend.service.rental.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;
    @PostMapping
    public RentalConfirmationDto rentMovie(@Valid @RequestBody RentalRequestDto dto) {
        return rentalService.rentMovie(dto);
    }

}
