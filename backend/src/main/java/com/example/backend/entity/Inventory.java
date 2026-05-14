package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.example.backend.entity.*;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

//    @Column(name = "film_id")
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "film_id")
//    private Film filmId;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
}
