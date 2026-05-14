package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "category")
@Getter
@Setter
public class Category {

    @Id
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "name")
    private String name;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
}