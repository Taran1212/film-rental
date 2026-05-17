package com.example.backend.service.staff;

import com.example.backend.dto.StaffRegisterDto;
import com.example.backend.dto.projection.StaffDetailProjection;
import com.example.backend.dto.projection.StaffProjection;
import com.example.backend.entity.City;
import com.example.backend.entity.Staff;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AddressRepository;
import com.example.backend.repository.CityRepository;
import com.example.backend.repository.StaffRepository;
import com.example.backend.util.AuthUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffService {

    private final StaffRepository staffRepository;
    private final AddressRepository addressRepository;
    private final CityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;

    @PersistenceContext
    private EntityManager entityManager;

    public Page<StaffProjection> getMyStoreStaff(Pageable pageable) {
        return staffRepository.findProjectedByStoreId(currentStoreId(), pageable);
    }

    public Page<StaffProjection> searchMyStoreStaff(String name, Pageable pageable) {
        Integer storeId = currentStoreId();
        return staffRepository
                .findProjectedByStoreIdAndFirstNameContainingIgnoreCaseOrStoreIdAndLastNameContainingIgnoreCaseOrStoreIdAndUsernameContainingIgnoreCase(
                        storeId, name, storeId, name, storeId, name, pageable);
    }

    public StaffDetailProjection getStaffById(Integer id) {
        return staffRepository.findByStaffIdAndStoreId(id, currentStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
    }

    @Transactional
    public String createStaff(StaffRegisterDto dto) {
        if (staffRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new ConflictException("Username '" + dto.getUsername() + "' is already taken");
        }
        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found"));

        Integer nextAddressId = addressRepository
                .findTopByOrderByAddressIdDesc()
                .map(a -> a.getAddressId() + 1)
                .orElse(1);

        entityManager.createNativeQuery(
                        "INSERT INTO address " +
                                "(address_id, address, address2, district, city_id, postal_code, phone, location, last_update) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ST_GeomFromText('POINT(0 0)'), ?)")
                .setParameter(1, nextAddressId)
                .setParameter(2, dto.getAddress())
                .setParameter(3, dto.getAddress2())
                .setParameter(4, dto.getDistrict())
                .setParameter(5, city.getCityId())
                .setParameter(6, dto.getPostalCode())
                .setParameter(7, dto.getPhone())
                .setParameter(8, LocalDateTime.now())
                .executeUpdate();

        Integer nextStaffId = staffRepository
                .findTopByOrderByStaffIdDesc()
                .map(s -> s.getStaffId() + 1)
                .orElse(1);

        Staff staff = new Staff();
        staff.setStaffId(nextStaffId);
        staff.setFirstName(dto.getFirstName());
        staff.setLastName(dto.getLastName());
        staff.setUsername(dto.getUsername());
        staff.setEmail(dto.getEmail());
        staff.setPassword(passwordEncoder.encode(dto.getPassword()));
        staff.setStoreId(dto.getStoreId());
        staff.setAddressId(nextAddressId);
        staff.setActive(true);
        staff.setLastUpdate(LocalDateTime.now());
        staffRepository.save(staff);

        return "Staff created successfully";
    }

    private Integer currentStoreId() {
        String username = authUtil.getLoggedInUsername();
        return staffRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"))
                .getStoreId();
    }
}