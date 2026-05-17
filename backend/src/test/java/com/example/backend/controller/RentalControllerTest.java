
package com.example.backend.controller;
import com.example.backend.controller.rental.RentalController;
import com.example.backend.dto.RentalConfirmationDto;
import com.example.backend.dto.RentalRequestDto;
import com.example.backend.dto.projection.RentalProjection;
import com.example.backend.exception.BadRequestException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @DisplayName("GET /api/rentals/active — paged projection")
    void shouldReturnActiveRentals() throws Exception {
        when(rentalService.getActiveRentals(any(), any()))
                .thenReturn(new PageImpl<>(List.of(rental(1, "ACADEMY DINOSAUR", "MARY SMITH"))));

        mockMvc.perform(get("/api/rentals/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].rentalId").value(1))
                .andExpect(jsonPath("$.content[0].movieTitle").value("ACADEMY DINOSAUR"))
                .andExpect(jsonPath("$.content[0].customerName").value("MARY SMITH"))
                .andExpect(jsonPath("$.content[0].returned").value(false));
    }

    @Test
    @DisplayName("GET /api/rentals/active?search=foo — service receives the search arg")
    void shouldPassSearchArg() throws Exception {
        when(rentalService.getActiveRentals(eq("foo"), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/rentals/active?search=foo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/rentals/customer/{id} — rentals scoped to customer")
    void shouldReturnCustomerRentals() throws Exception {
        when(rentalService.getCustomerRentals(eq(1), any()))
                .thenReturn(new PageImpl<>(List.of(rental(1, "ACADEMY DINOSAUR", "MARY SMITH"))));

        mockMvc.perform(get("/api/rentals/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].customerName").value("MARY SMITH"));
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

    @Test
    @DisplayName("POST /api/rentals — empty body → 400 with field errors")
    void shouldRejectEmptyBody() throws Exception {
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.inventoryId").exists())
                .andExpect(jsonPath("$.fieldErrors.customerId").exists())
                .andExpect(jsonPath("$.fieldErrors.staffId").exists());
    }

    @Test
    @DisplayName("POST /api/rentals — negative IDs → 400")
    void shouldRejectNegativeIds() throws Exception {
        RentalRequestDto req = new RentalRequestDto();
        req.setInventoryId(-1);
        req.setCustomerId(0);
        req.setStaffId(-99);

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.inventoryId").exists())
                .andExpect(jsonPath("$.fieldErrors.customerId").exists())
                .andExpect(jsonPath("$.fieldErrors.staffId").exists());
    }

    @Test
    @DisplayName("POST /api/rentals — already-rented copy → 400 via BadRequestException")
    void shouldReturn400OnAlreadyRented() throws Exception {
        RentalRequestDto req = new RentalRequestDto();
        req.setInventoryId(1);
        req.setCustomerId(1);
        req.setStaffId(1);

        when(rentalService.rentMovie(any()))
                .thenThrow(new BadRequestException("Movie copy is currently unavailable"));

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Movie copy is currently unavailable"));
    }

    @Test
    @DisplayName("PUT /api/rentals/return/{id} — happy path")
    void shouldReturnMovie() throws Exception {
        when(rentalService.returnMovie(1)).thenReturn("Movie returned successfully");

        mockMvc.perform(put("/api/rentals/return/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie returned successfully"));
    }
}