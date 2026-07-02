package com.ctdecomerce.store.product.model;

import com.ctdecomerce.store.retailers.model.RetailersModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "products")
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Getter
    @Setter
    @Column()
    private String name;

    @Getter
    @Setter
    @Column()
    private String description;

    @Getter
    @Setter
    @Column()
    private int priceInCents;

    @Getter
    @Setter
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private RetailersModel owner;
}
