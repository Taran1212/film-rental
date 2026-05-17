
package com.example.backend.controller;
import com.example.backend.controller.rental.RentalController;
import com.example.backend.dto.RentalConfirmationDto;
import com.example.backend.dto.RentalRequestDto;
import com.example.backend.dto.projection.RentalProjection;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.security.CustomStaffDetailsService;
import com.example.backend.security.JwtService;
import com.example.backend.service.rental.RentalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RentalController.class, excludeAutoConfiguration = {
        org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration.class,
        org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private final ObjectMapper json = new ObjectMapper();

    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomStaffDetailsService userDetailsService;
    @MockitoBean private RentalService rentalService;

    private RentalProjection rental(int id, String movie, String customer) {
        return new RentalProjection() {
            public Integer getRentalId() { return id; }
            public String getMovieTitle() { return movie; }
            public String getCustomerName() { return customer; }
            public LocalDateTime getRentalDate() { return LocalDateTime.of(2026, 1, 1, 10, 0); }
            public LocalDateTime getReturnDate() { return null; }
            public Boolean getReturned() { return false; }
        };
    }

    @Test
    @DisplayName("POST /api/rentals — happy path returns RentalConfirmationDto")
    void shouldRentMovie() throws Exception {
        RentalRequestDto req = new RentalRequestDto();
        req.setInventoryId(1);
        req.setCustomerId(1);
        req.setStaffId(1);

        RentalConfirmationDto conf = RentalConfirmationDto.builder()
                .rentalId(100).movieTitle("ACADEMY DINOSAUR").customerName("MARY SMITH")
                .copyId(1).amount(new BigDecimal("0.99")).build();
        when(rentalService.rentMovie(any())).thenReturn(conf);

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalId").value(100))
                .andExpect(jsonPath("$.movieTitle").value("ACADEMY DINOSAUR"))
                .andExpect(jsonPath("$.amount").value(0.99));
    }
}