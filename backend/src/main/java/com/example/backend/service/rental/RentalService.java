package com.example.backend.service.rental;

import com.example.backend.dto.RentalConfirmationDto;
import com.example.backend.dto.RentalRequestDto;
import com.example.backend.dto.projection.RentalProjection;
import com.example.backend.entity.*;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.InventoryRepository;
import com.example.backend.repository.RentalRepository;
import com.example.backend.repository.StaffRepository;
import com.example.backend.service.payment.PaymentService;
import com.example.backend.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalService {

    private final RentalRepository rentalRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final PaymentService paymentService;
    private final AuthUtil authUtil;

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

    @Transactional
    public String returnMovie(Integer rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found"));
        rental.setReturnDate(LocalDateTime.now());
        rental.setLastUpdate(LocalDateTime.now());
        rentalRepository.save(rental);
        return "Movie returned successfully";
    }

    public Page<RentalProjection> getActiveRentals(String search, Pageable pageable) {
        Integer storeId = currentStoreId();

        if (search == null || search.trim().isEmpty()) {
            return rentalRepository.findByStaff_StoreIdAndReturnDateIsNull(storeId, pageable);
        }

        String q = search.trim();
        String[] parts = q.split("\\s+");

        Page<RentalProjection> page = rentalRepository
                .findByStaff_StoreIdAndReturnDateIsNullAndInventory_Film_TitleContainingIgnoreCaseOrStaff_StoreIdAndReturnDateIsNullAndCustomer_FirstNameContainingIgnoreCaseOrStaff_StoreIdAndReturnDateIsNullAndCustomer_LastNameContainingIgnoreCase(
                        storeId, q, storeId, q, storeId, q, pageable);

        if (parts.length >= 2 && page.isEmpty()) {
            return rentalRepository
                    .findByStaff_StoreIdAndReturnDateIsNullAndCustomer_FirstNameContainingIgnoreCaseAndCustomer_LastNameContainingIgnoreCase(
                            storeId, parts[0], parts[parts.length - 1], pageable);
        }
        return page;
    }

    public Page<RentalProjection> getCustomerRentals(Integer customerId, Pageable pageable) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found");
        }
        return rentalRepository.findByCustomer_CustomerId(customerId, pageable);
    }

    private Integer currentStoreId() {
        String username = authUtil.getLoggedInUsername();
        return staffRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"))
                .getStoreId();
    }
}
