package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "store")
@Getter
@Setter
public class Store {

    @Id
    @Column(name = "store_id")
    private Integer storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_staff_id", insertable = false, updatable = false)
    @JsonIgnore
    private Staff managerStaff;

    @Column(name = "address_id")
    private Integer addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", insertable = false, updatable = false)
    @JsonIgnore
    private Address address;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Staff> staffMembers;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Customer> customers;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Inventory> inventories;
}