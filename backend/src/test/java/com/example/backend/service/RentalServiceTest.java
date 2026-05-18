package com.example.backend.service;

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
import com.example.backend.service.rental.RentalService;
import com.example.backend.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;




@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {


    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private AuthUtil authUtil;

    @InjectMocks
    private RentalService rentalService;

    private RentalRequestDto request;
    private Inventory inventory;
    private Film film;
    private Customer customer;
    private Staff staff;

    @BeforeEach
    void setUp() {
        request = new RentalRequestDto();
        request.setInventoryId(1);
        request.setCustomerId(2);
        request.setStaffId(3);

        film = new Film();
        film.setFilmId(10);
        film.setTitle("ACADEMY DINOSAUR");
        film.setRentalRate(BigDecimal.valueOf(4.99));

        inventory = new Inventory();
        inventory.setInventoryId(1);
        inventory.setFilm(film);

        customer = new Customer();
        customer.setCustomerId(2);
        customer.setFirstName("Alice");
        customer.setLastName("Smith");

        staff = new Staff();
        staff.setStaffId(3);
        staff.setStoreId(1);
    }

    private RentalProjection rentalProjection(Integer id, String movie) {
        return new RentalProjection() {
            @Override
            public Integer getRentalId() {
                return id;
            }

            @Override
            public String getMovieTitle() {
                return movie;
            }

            @Override
            public String getCustomerName() {
                return "Alice Smith";
            }

            @Override
            public LocalDateTime getRentalDate() {
                return LocalDateTime.now();
            }

            @Override
            public LocalDateTime getReturnDate() {
                return null;
            }

            @Override
            public Boolean getReturned() {
                return false;
            }
        };
    }

    @Test
    @DisplayName("rentMovie — should create rental and payment successfully")
    void shouldRentMovieSuccessfully() {
        when(inventoryRepository.findById(1)).thenReturn(Optional.of(inventory));
        when(rentalRepository.existsByInventoryAndReturnDateIsNull(inventory)).thenReturn(false);
        when(customerRepository.findById(2)).thenReturn(Optional.of(customer));
        when(staffRepository.findById(3)).thenReturn(Optional.of(staff));

        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> {
            Rental r = invocation.getArgument(0);
            r.setRentalId(500);
            return r;
        });

        Payment payment = new Payment();
        payment.setAmount(BigDecimal.valueOf(4.99));
        when(paymentService.createPaymentForRental(
                any(Rental.class), eq(customer), eq(staff), eq(inventory)))
                .thenReturn(payment);

        RentalConfirmationDto dto = rentalService.rentMovie(request);

        assertThat(dto.getRentalId()).isEqualTo(500);
        assertThat(dto.getMovieTitle()).isEqualTo("ACADEMY DINOSAUR");
        assertThat(dto.getCustomerName()).isEqualTo("Alice Smith");
        assertThat(dto.getCopyId()).isEqualTo(1);
        assertThat(dto.getAmount()).isEqualByComparingTo("4.99");

        ArgumentCaptor<Rental> captor = ArgumentCaptor.forClass(Rental.class);
        verify(rentalRepository).save(captor.capture());
        Rental saved = captor.getValue();
        assertThat(saved.getInventory()).isEqualTo(inventory);
        assertThat(saved.getCustomer()).isEqualTo(customer);
        assertThat(saved.getStaff()).isEqualTo(staff);
        assertThat(saved.getRentalDate()).isNotNull();
    }

    @Test
    @DisplayName("rentMovie — should throw when inventory not found")
    void shouldThrowWhenInventoryMissing() {
        when(inventoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentalService.rentMovie(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Inventory not found");

        verify(rentalRepository, never()).save(any());
    }

    @Test
    @DisplayName("rentMovie — should throw when copy is currently rented")
    void shouldThrowWhenCopyUnavailable() {
        when(inventoryRepository.findById(1)).thenReturn(Optional.of(inventory));
        when(rentalRepository.existsByInventoryAndReturnDateIsNull(inventory)).thenReturn(true);

        assertThatThrownBy(() -> rentalService.rentMovie(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Movie copy is currently unavailable");
    }

    @Test
    @DisplayName("rentMovie — should throw when customer not found")
    void shouldThrowWhenCustomerMissing() {
        when(inventoryRepository.findById(1)).thenReturn(Optional.of(inventory));
        when(rentalRepository.existsByInventoryAndReturnDateIsNull(inventory)).thenReturn(false);
        when(customerRepository.findById(2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentalService.rentMovie(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer not found");
    }

    @Test
    @DisplayName("rentMovie — should throw when staff not found")
    void shouldThrowWhenStaffMissingDuringRent() {
        when(inventoryRepository.findById(1)).thenReturn(Optional.of(inventory));
        when(rentalRepository.existsByInventoryAndReturnDateIsNull(inventory)).thenReturn(false);
        when(customerRepository.findById(2)).thenReturn(Optional.of(customer));
        when(staffRepository.findById(3)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentalService.rentMovie(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Staff not found");
    }

    @Test
    @DisplayName("returnMovie — should set return date and save")
    void shouldReturnMovie() {
        Rental rental = new Rental();
        rental.setRentalId(1);

        when(rentalRepository.findById(1)).thenReturn(Optional.of(rental));

        String result = rentalService.returnMovie(1);

        assertThat(result).isEqualTo("Movie returned successfully");
        assertThat(rental.getReturnDate()).isNotNull();
        verify(rentalRepository).save(rental);
    }

    @Test
    @DisplayName("returnMovie — should throw when rental not found")
    void shouldThrowWhenReturnRentalMissing() {
        when(rentalRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentalService.returnMovie(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Rental not found");
    }

    @Test
    @DisplayName("getActiveRentals — should return all active when no search term")
    void shouldReturnActiveRentalsWithoutSearch() {
        when(authUtil.getLoggedInUsername()).thenReturn("user");
        when(staffRepository.findByUsername("user")).thenReturn(Optional.of(staff));

        Page<RentalProjection> expected = new PageImpl<>(List.of(
                rentalProjection(1, "ACADEMY DINOSAUR")));

        when(rentalRepository.findByStaff_StoreIdAndReturnDateIsNull(eq(1), any(PageRequest.class)))
                .thenReturn(expected);

        Page<RentalProjection> result =
                rentalService.getActiveRentals(null, PageRequest.of(0, 5));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("getActiveRentals — should run combined search when term supplied")
    void shouldSearchActiveRentalsWithCombinedQuery() {
        when(authUtil.getLoggedInUsername()).thenReturn("user");
        when(staffRepository.findByUsername("user")).thenReturn(Optional.of(staff));

        Page<RentalProjection> expected = new PageImpl<>(List.of(
                rentalProjection(1, "ACADEMY DINOSAUR")));

        when(rentalRepository
                .findByStaff_StoreIdAndReturnDateIsNullAndInventory_Film_TitleContainingIgnoreCaseOrStaff_StoreIdAndReturnDateIsNullAndCustomer_FirstNameContainingIgnoreCaseOrStaff_StoreIdAndReturnDateIsNullAndCustomer_LastNameContainingIgnoreCase(
                        eq(1), eq("acad"), eq(1), eq("acad"), eq(1), eq("acad"),
                        any(PageRequest.class)))
                .thenReturn(expected);

        Page<RentalProjection> result =
                rentalService.getActiveRentals("acad", PageRequest.of(0, 5));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("getCustomerRentals — should return rentals when customer exists")
    void shouldReturnCustomerRentals() {
        when(customerRepository.existsById(2)).thenReturn(true);

        Page<RentalProjection> expected = new PageImpl<>(List.of(
                rentalProjection(1, "ACADEMY DINOSAUR")));

        when(rentalRepository.findByCustomer_CustomerId(eq(2), any(PageRequest.class)))
                .thenReturn(expected);

        Page<RentalProjection> result =
                rentalService.getCustomerRentals(2, PageRequest.of(0, 5));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("getCustomerRentals — should throw when customer missing")
    void shouldThrowWhenCustomerMissingInRentalsLookup() {
        when(customerRepository.existsById(999)).thenReturn(false);

        assertThatThrownBy(() ->
                rentalService.getCustomerRentals(999, PageRequest.of(0, 5)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer not found");
    }
}
