package com.ctdecomerce.store.product.model;

import com.ctdecomerce.store.delivery.model.LocationModel;
import com.ctdecomerce.store.retailers.model.RetailersModel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "products")
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column()
    private String name;

    @Column()
    private String description;

    @Column()
    private int priceInCents;

    @Column(name = "stock")
    private int stock;

    @Column(name = "isAvailable")
    private boolean isAvailable;

    @Column(name = "isShowing")
    private boolean isShowing;

    @ManyToOne()
    @JoinColumn()
    private LocationModel location;

    @ManyToOne()
    @JoinColumn()
    @JsonBackReference
    private RetailersModel owner;

}
