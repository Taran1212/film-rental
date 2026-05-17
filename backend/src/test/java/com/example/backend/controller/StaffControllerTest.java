package com.example.backend.controller;

import com.example.backend.controller.staff.StaffController;
import com.example.backend.security.CustomStaffDetailsService;
import com.example.backend.security.JwtService;
import tools.jackson.databind.ObjectMapper;
import com.example.backend.dto.StaffRegisterDto;
import com.example.backend.dto.projection.StaffDetailProjection;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.service.StaffService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StaffController.class, excludeAutoConfiguration = {
        org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration.class,
        org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class StaffControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper json;

    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomStaffDetailsService userDetailsService;
    @MockitoBean private StaffService staffService;

    private StaffDetailProjection detail() {
        return new StaffDetailProjection() {
            public Integer getStaffId() { return 1; }
            public String getFullName() { return "Mike Hillyer"; }
            public String getUsername() { return "Mike"; }
            public String getEmail() { return "Mike.Hillyer@x.com"; }
            public Integer getStoreId() { return 1; }
            public Boolean getActive() { return true; }
            public String getAddress() { return "23 Workhaven Lane"; }
            public String getAddress2() { return null; }
            public String getDistrict() { return "Alberta"; }
            public String getCity() { return "Lethbridge"; }
            public String getCountry() { return "Canada"; }
            public String getPostalCode() { return "14400"; }
            public String getPhone() { return "+1 234"; }
        };
    }

    private StaffRegisterDto validRequest() {
        StaffRegisterDto r = new StaffRegisterDto();
        r.setFirstName("Alice");
        r.setLastName("Doe");
        r.setUsername("alice_d");
        r.setEmail("alice@example.com");
        r.setPassword("secret123");
        r.setStoreId(1);
        r.setAddress("123 Main St");
        r.setDistrict("D1");
        r.setCityId(1);
        r.setPhone("+1 555");
        return r;
    }

    @Test
    @DisplayName("GET /api/staff/{id} — detail projection with flattened address")
    void shouldReturnDetailWithAddress() throws Exception {
        when(staffService.getStaffById(1, 1)).thenReturn(detail());

        mockMvc.perform(get("/api/staff/1").param("storeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Mike Hillyer"))
                .andExpect(jsonPath("$.username").value("Mike"))
                .andExpect(jsonPath("$.city").value("Lethbridge"))
                .andExpect(jsonPath("$.country").value("Canada"));
    }

    @Test
    @DisplayName("POST /api/staff — valid body returns service-supplied message")
    void shouldCreateStaff() throws Exception {
        when(staffService.createStaff(any())).thenReturn("Staff created successfully");

        mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(validRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Staff created successfully"));
    }

    @Test
    @DisplayName("POST /api/staff — invalid username pattern → 400")
    void shouldRejectBadUsername() throws Exception {
        StaffRegisterDto r = validRequest();
        r.setUsername("has spaces!");

        mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(r)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/staff — missing password → 400")
    void shouldRejectMissingPassword() throws Exception {
        StaffRegisterDto r = validRequest();
        r.setPassword(null);

        mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(r)))
                .andExpect(status().isBadRequest());
    }
}