package com.example.backend.service;

import com.example.backend.dto.StaffRegisterDto;
import com.example.backend.dto.projection.StaffDetailProjection;
import com.example.backend.dto.projection.StaffProjection;
import com.example.backend.entity.Address;
import com.example.backend.entity.City;
import com.example.backend.entity.Staff;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AddressRepository;
import com.example.backend.repository.CityRepository;
import com.example.backend.repository.StaffRepository;
import com.example.backend.service.staff.StaffService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private StaffService staffService;

    private StaffRegisterDto registerDto;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(staffService, "entityManager", entityManager);

        registerDto = new StaffRegisterDto();
        registerDto.setFirstName("Alice");
        registerDto.setLastName("Smith");
        registerDto.setUsername("alice");
        registerDto.setEmail("alice@example.com");
        registerDto.setPassword("plain");
        registerDto.setStoreId(1);
        registerDto.setAddress("123 Main");
        registerDto.setAddress2("Apt 1");
        registerDto.setDistrict("Punjab");
        registerDto.setCityId(1);
        registerDto.setPostalCode("140413");
        registerDto.setPhone("9876543210");
    }

    private Staff staff(Integer storeId) {
        Staff s = new Staff();
        s.setStoreId(storeId);
        s.setUsername("user");
        return s;
    }

    private StaffProjection projection(Integer id, String name) {
        return new StaffProjection() {
            @Override
            public Integer getStaffId() {
                return id;
            }

            @Override
            public String getFullName() {
                return name;
            }

            @Override
            public String getEmail() {
                return "alice@example.com";
            }

            @Override
            public Boolean getActive() {
                return true;
            }
        };
    }

    @Test
    @DisplayName("getMyStoreStaff — should return paged projections for current store")
    void shouldReturnStoreStaff() {
        when(authUtil.getLoggedInUsername()).thenReturn("user");
        when(staffRepository.findByUsername("user")).thenReturn(Optional.of(staff(1)));

        Page<StaffProjection> expected = new PageImpl<>(List.of(
                projection(1, "Alice Smith")));

        when(staffRepository.findProjectedByStoreId(eq(1), any(PageRequest.class)))
                .thenReturn(expected);

        Page<StaffProjection> result =
                staffService.getMyStoreStaff(PageRequest.of(0, 5));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFullName()).isEqualTo("Alice Smith");
    }

    @Test
    @DisplayName("searchMyStoreStaff — should delegate to combined search query")
    void shouldSearchStoreStaff() {
        when(authUtil.getLoggedInUsername()).thenReturn("user");
        when(staffRepository.findByUsername("user")).thenReturn(Optional.of(staff(1)));

        Page<StaffProjection> expected = new PageImpl<>(List.of(
                projection(1, "Alice Smith")));

        when(staffRepository
                .findProjectedByStoreIdAndFirstNameContainingIgnoreCaseOrStoreIdAndLastNameContainingIgnoreCaseOrStoreIdAndUsernameContainingIgnoreCase(
                        eq(1), eq("alice"), eq(1), eq("alice"), eq(1), eq("alice"),
                        any(PageRequest.class)))
                .thenReturn(expected);

        Page<StaffProjection> result =
                staffService.searchMyStoreStaff("alice", PageRequest.of(0, 5));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("getStaffById — should return projection when staff in same store")
    void shouldReturnStaffById() {
        when(authUtil.getLoggedInUsername()).thenReturn("user");
        when(staffRepository.findByUsername("user")).thenReturn(Optional.of(staff(1)));

        StaffDetailProjection detail = new StaffDetailProjection() {
            @Override
            public Integer getStaffId() { return 5; }
            @Override
            public String getFullName() { return "Alice Smith"; }
            @Override
            public String getUsername() { return "alice"; }
            @Override
            public String getEmail() { return "alice@example.com"; }
            @Override
            public Integer getStoreId() { return 1; }
            @Override
            public Boolean getActive() { return true; }
            @Override
            public String getAddress() { return "123 Main"; }
            @Override
            public String getAddress2() { return null; }
            @Override
            public String getDistrict() { return "Punjab"; }
            @Override
            public String getCity() { return "City"; }
            @Override
            public String getCountry() { return "Country"; }
            @Override
            public String getPostalCode() { return "140413"; }
            @Override
            public String getPhone() { return "9876543210"; }
        };

        when(staffRepository.findByStaffIdAndStoreId(5, 1))
                .thenReturn(Optional.of(detail));

        StaffDetailProjection result = staffService.getStaffById(5);

        assertThat(result.getStaffId()).isEqualTo(5);
        assertThat(result.getFullName()).isEqualTo("Alice Smith");
    }

    @Test
    @DisplayName("getStaffById — should throw when not in same store")
    void shouldThrowWhenStaffNotInStore() {
        when(authUtil.getLoggedInUsername()).thenReturn("user");
        when(staffRepository.findByUsername("user")).thenReturn(Optional.of(staff(1)));
        when(staffRepository.findByStaffIdAndStoreId(99, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> staffService.getStaffById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Staff not found");
    }

    @Test
    @DisplayName("createStaff — should save staff with encoded password and next id")
    void shouldCreateStaffSuccessfully() {
        when(staffRepository.findByUsername("alice")).thenReturn(Optional.empty());

        City city = new City();
        city.setCityId(1);
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));

        Address address = new Address();
        address.setAddressId(50);
        when(addressRepository.findTopByOrderByAddressIdDesc()).thenReturn(Optional.of(address));

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyInt(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        Staff lastStaff = new Staff();
        lastStaff.setStaffId(20);
        when(staffRepository.findTopByOrderByStaffIdDesc()).thenReturn(Optional.of(lastStaff));

        when(passwordEncoder.encode("plain")).thenReturn("encoded");

        when(staffRepository.save(any(Staff.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = staffService.createStaff(registerDto);

        assertThat(result).isEqualTo("Staff created successfully");

        ArgumentCaptor<Staff> captor = ArgumentCaptor.forClass(Staff.class);
        verify(staffRepository).save(captor.capture());
        Staff saved = captor.getValue();
        assertThat(saved.getStaffId()).isEqualTo(21);
        assertThat(saved.getAddressId()).isEqualTo(51);
        assertThat(saved.getUsername()).isEqualTo("alice");
        assertThat(saved.getPassword()).isEqualTo("encoded");
        assertThat(saved.getActive()).isTrue();
    }

    @Test
    @DisplayName("createStaff — should throw when username already taken")
    void shouldThrowWhenUsernameTaken() {
        when(staffRepository.findByUsername("alice")).thenReturn(Optional.of(new Staff()));

        assertThatThrownBy(() -> staffService.createStaff(registerDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("alice");

        verify(staffRepository, never()).save(any());
    }

    @Test
    @DisplayName("createStaff — should throw when city not found")
    void shouldThrowWhenCityNotFoundDuringCreate() {
        when(staffRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(cityRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> staffService.createStaff(registerDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("City not found");
    }
}
