package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "film")
@Getter
@Setter
public class Film {

    @Id
    @Column(name = "film_id")
    private Integer filmId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "release_year")
    private String releaseYear;

    @Column(name = "rental_duration")
    private Integer rentalDuration;

    @Column(name = "rental_rate")
    private BigDecimal rentalRate;

    @Column(name = "length")
    private Integer length;

    @Column(name = "replacement_cost")
    private BigDecimal replacementCost;

    @Column(name = "rating")
    private String rating;

    @Column(name = "special_features")
    private String specialFeatures;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    @OneToMany(mappedBy = "film", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FilmActor> filmActors;

    @OneToMany(mappedBy = "film", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FilmCategory> filmCategories;

    @OneToMany(mappedBy = "film", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Inventory> inventories;
}