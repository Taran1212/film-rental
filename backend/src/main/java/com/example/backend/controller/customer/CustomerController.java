package com.example.backend.controller.customer;


import com.example.backend.dto.CustomerRequestDto;
import com.example.backend.dto.projection.CustomerProjection;
import com.example.backend.service.customer.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STAFF')")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public String addCustomer(@Valid @RequestBody CustomerRequestDto dto) {
        return customerService.addCustomer(dto);
    }

    @GetMapping
    public Page<CustomerProjection> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return customerService.getAllCustomers(PageRequest.of(page, size));
    }

    @GetMapping("/search")
    public Page<CustomerProjection> searchCustomers(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return customerService.searchCustomers(name, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public CustomerProjection getCustomerById(@PathVariable Integer id) {
        return customerService.getCustomerById(id);
    }
}
