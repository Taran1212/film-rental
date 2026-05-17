package com.example.backend.service.rental;

import com.example.backend.dto.RentalConfirmationDto;
import com.example.backend.dto.RentalRequestDto;
import com.example.backend.entity.*;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.InventoryRepository;
import com.example.backend.repository.RentalRepository;
import com.example.backend.repository.StaffRepository;
import com.example.backend.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalService {

    private final InventoryRepository inventoryRepository;
    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final PaymentService paymentService;

    @Transactional
    public RentalConfirmationDto rentMovie(RentalRequestDto dto) {

        Inventory inventory = inventoryRepository.findById(dto.getInventoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        if (rentalRepository.existsByInventoryAndReturnDateIsNull(inventory)) {
            throw new BadRequestException("Movie copy is currently unavailable");
        }

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Staff staff = staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        Rental latestRental = rentalRepository.findTopByOrderByRentalIdDesc();

        int nextRentalId = latestRental == null
                ? 1
                : latestRental.getRentalId() + 1;

        Rental rental = new Rental();
        rental.setRentalId(nextRentalId);
        rental.setRentalDate(LocalDateTime.now());
        rental.setInventory(inventory);
        rental.setCustomer(customer);
        rental.setStaff(staff);
        rental.setLastUpdate(LocalDateTime.now());

        rentalRepository.save(rental);

        // Payment creation lives in payment module
        Payment payment = paymentService.createPaymentForRental(
                rental,
                customer,
                staff,
                inventory
        );

        return RentalConfirmationDto.builder()
                .rentalId(rental.getRentalId())
                .movieTitle(inventory.getFilm().getTitle())
                .customerName(customer.getFirstName() + " " + customer.getLastName())
                .copyId(inventory.getInventoryId())
                .amount(payment.getAmount())
                .build();
    }
}