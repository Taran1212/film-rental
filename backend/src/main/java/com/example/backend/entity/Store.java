package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "store")
@Getter
@Setter
public class Store {

    @Id
    @Column(name = "store_id")
    private Integer storeId;

    @Column(name = "manager_staff_id")
    private Integer managerStaffId;

    @Column(name = "address_id")
    private Integer addressId;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
}