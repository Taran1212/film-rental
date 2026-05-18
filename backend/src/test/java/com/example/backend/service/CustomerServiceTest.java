package com.example.backend.service;

import com.example.backend.dto.CustomerRequestDto;
import com.example.backend.dto.projection.CustomerProjection;
import com.example.backend.entity.*;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AddressRepository;
import com.example.backend.repository.CityRepository;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.StaffRepository;
import com.example.backend.service.customer.CustomerService;
import com.example.backend.util.AuthUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequestDto requestDto;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(
                customerService,
                "entityManager",
                entityManager
        );

        requestDto = new CustomerRequestDto();
        requestDto.setFirstName("Alice");
        requestDto.setLastName("Smith");
        requestDto.setEmail("alice@example.com");
        requestDto.setStoreId(1);

        requestDto.setAddress("123 Main Street");
        requestDto.setAddress2("Flat 101");
        requestDto.setDistrict("Punjab");
        requestDto.setCityId(1);
        requestDto.setPostalCode("140413");
        requestDto.setPhone("9876543210");
    }

    private Staff mockStaff(Integer storeId) {
        Staff staff = new Staff();
        staff.setStoreId(storeId);
        return staff;
    }

    private CustomerProjection projection(
            Integer id,
            String fullName,
            String email,
            Integer storeId
    ) {
        return new CustomerProjection() {
            @Override
            public Integer getCustomerId() {
                return id;
            }

            @Override
            public String getFullName() {
                return fullName;
            }

            @Override
            public String getEmail() {
                return email;
            }

            @Override
            public Integer getStoreId() {
                return storeId;
            }

            @Override
            public Boolean getActive() {
                return true;
            }
        };
    }

    @Test
    @DisplayName("addCustomer — should save customer successfully")
    void shouldAddCustomerSuccessfully() {

        City city = new City();
        city.setCityId(1);

        Address address = new Address();
        address.setAddressId(100);

        when(cityRepository.findById(1)).thenReturn(Optional.of(city));

        when(addressRepository.findTopByOrderByAddressIdDesc())
                .thenReturn(Optional.of(address));

        when(entityManager.createNativeQuery(anyString()))
                .thenReturn(query);

        when(query.setParameter(anyInt(), any()))
                .thenReturn(query);

        when(query.executeUpdate())
                .thenReturn(1);

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> {
                    Customer c = invocation.getArgument(0);
                    c.setCustomerId(101);
                    return c;
                });

        String result = customerService.addCustomer(requestDto);

        assertThat(result).isEqualTo("101");

        ArgumentCaptor<Customer> captor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerRepository).save(captor.capture());

        Customer savedCustomer = captor.getValue();

        assertThat(savedCustomer.getFirstName()).isEqualTo("Alice");
        assertThat(savedCustomer.getLastName()).isEqualTo("Smith");
        assertThat(savedCustomer.getEmail()).isEqualTo("alice@example.com");
        assertThat(savedCustomer.getStore().getStoreId()).isEqualTo(1);
        assertThat(savedCustomer.getAddress().getAddressId()).isEqualTo(101);
        assertThat(savedCustomer.getActive()).isTrue();
    }

    @Test
    @DisplayName("addCustomer — should throw when city not found")
    void shouldThrowWhenCityNotFound() {

        when(cityRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                customerService.addCustomer(requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("City not found");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("getAllCustomers — should return paged projections")
    void shouldReturnAllCustomers() {

        when(authUtil.getLoggedInUsername())
                .thenReturn("staff");

        when(staffRepository.findByUsername("staff"))
                .thenReturn(Optional.of(mockStaff(1)));

        Page<CustomerProjection> expected =
                new PageImpl<>(List.of(
                        projection(1, "MARY SMITH", "mary@test.com", 1)
                ));

        when(customerRepository.findProjectedByStore_StoreId(
                eq(1),
                any(PageRequest.class)
        )).thenReturn(expected);

        Page<CustomerProjection> result =
                customerService.getAllCustomers(PageRequest.of(0, 5));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFullName())
                .isEqualTo("MARY SMITH");
    }

    @Test
    @DisplayName("searchCustomers — should search using AND query for full name")
    void shouldSearchByFullName() {

        when(authUtil.getLoggedInUsername())
                .thenReturn("staff");

        when(staffRepository.findByUsername("staff"))
                .thenReturn(Optional.of(mockStaff(1)));

        Page<CustomerProjection> expected =
                new PageImpl<>(List.of(
                        projection(1, "MARY SMITH", "mary@test.com", 1)
                ));

        when(customerRepository
                .findProjectedByStore_StoreIdAndFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                        eq(1),
                        eq("mary"),
                        eq("smith"),
                        any(PageRequest.class)
                )).thenReturn(expected);

        Page<CustomerProjection> result =
                customerService.searchCustomers(
                        "mary smith",
                        PageRequest.of(0, 5)
                );

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getFullName())
                .containsIgnoringCase("MARY");
    }

    @Test
    @DisplayName("searchCustomers — should search using OR query for single name")
    void shouldSearchBySingleName() {

        when(authUtil.getLoggedInUsername())
                .thenReturn("staff");

        when(staffRepository.findByUsername("staff"))
                .thenReturn(Optional.of(mockStaff(1)));

        Page<CustomerProjection> expected =
                new PageImpl<>(List.of(
                        projection(1, "MARY SMITH", "mary@test.com", 1)
                ));

        when(customerRepository
                .findProjectedByStore_StoreIdAndFirstNameContainingIgnoreCaseOrStore_StoreIdAndLastNameContainingIgnoreCase(
                        eq(1),
                        eq("mary"),
                        eq(1),
                        eq("mary"),
                        any(PageRequest.class)
                )).thenReturn(expected);

        Page<CustomerProjection> result =
                customerService.searchCustomers(
                        "mary",
                        PageRequest.of(0, 5)
                );

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getFullName())
                .containsIgnoringCase("MARY");
    }

    @Test
    @DisplayName("getCustomerById — should return projection")
    void shouldReturnCustomerById() {

        CustomerProjection projection =
                projection(1, "MARY SMITH", "mary@test.com", 1);

        when(customerRepository.findProjectedByCustomerId(1))
                .thenReturn(Optional.of(projection));

        CustomerProjection result =
                customerService.getCustomerById(1);

        assertThat(result.getCustomerId()).isEqualTo(1);
        assertThat(result.getFullName())
                .isEqualTo("MARY SMITH");
    }

    @Test
    @DisplayName("getCustomerById — should throw when customer not found")
    void shouldThrowWhenCustomerNotFound() {

        when(customerRepository.findProjectedByCustomerId(999))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                customerService.getCustomerById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer not found");
    }

    @Test
    @DisplayName("currentStoreId — should throw when staff not found")
    void shouldThrowWhenStaffNotFound() {

        when(authUtil.getLoggedInUsername())
                .thenReturn("unknown");

        when(staffRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                customerService.getAllCustomers(PageRequest.of(0, 5)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Staff not found");
    }
}