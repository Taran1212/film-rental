package com.example.backend.controller;

import com.example.backend.controller.inventory.InventoryController;
import com.example.backend.dto.InventoryDto;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.security.CustomStaffDetailsService;
import com.example.backend.security.JwtService;
import com.example.backend.service.inventory.InventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InventoryController.class, excludeAutoConfiguration = {
        org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration.class,
        org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class InventoryControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomStaffDetailsService userDetailsService;
    @MockitoBean private InventoryService inventoryService;

    @Test
    @DisplayName("GET /api/movies/inventory — paged InventoryDto")
    void shouldReturnStoreInventory() throws Exception {
        InventoryDto inv = InventoryDto.builder()
                .filmId(1).movieTitle("ACADEMY DINOSAUR")
                .totalCopies(3L).rentedCopies(1L).availableCopies(2L)
                .build();
        when(inventoryService.getStoreInventory(any(), any()))
                .thenReturn(new PageImpl<>(List.of(inv)));

        mockMvc.perform(get("/api/movies/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].movieTitle").value("ACADEMY DINOSAUR"))
                .andExpect(jsonPath("$.content[0].totalCopies").value(3))
                .andExpect(jsonPath("$.content[0].availableCopies").value(2));
    }

    @Test
    @DisplayName("GET /api/movies/{id}/inventory — single inventory dto")
    void shouldReturnSingleInventory() throws Exception {
        InventoryDto inv = InventoryDto.builder()
                .filmId(1).movieTitle("ACADEMY DINOSAUR")
                .totalCopies(3L).rentedCopies(1L).availableCopies(2L)
                .build();
        when(inventoryService.getInventory(1)).thenReturn(inv);

        mockMvc.perform(get("/api/movies/1/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filmId").value(1))
                .andExpect(jsonPath("$.availableCopies").value(2));
    }

    @Test
    @DisplayName("GET /api/movies/{id}/inventory/next-available — available copy found")
    void shouldReturnNextAvailableCopy() throws Exception {
        when(inventoryService.findFirstAvailableInventoryId(1)).thenReturn(42);

        mockMvc.perform(get("/api/movies/1/inventory/next-available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inventoryId").value(42))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("GET /api/movies/{id}/inventory/next-available — no copy available → null + false")
    void shouldReturnNullWhenNoCopyAvailable() throws Exception {
        when(inventoryService.findFirstAvailableInventoryId(1)).thenReturn(null);

        mockMvc.perform(get("/api/movies/1/inventory/next-available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inventoryId").doesNotExist())
                .andExpect(jsonPath("$.available").value(false));
    }
}
