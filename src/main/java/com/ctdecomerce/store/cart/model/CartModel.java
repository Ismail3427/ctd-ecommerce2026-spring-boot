package com.ctdecomerce.store.cart.model;

import com.ctdecomerce.store.product.model.ProductModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "cart")
public class CartModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column()
    private String userId;

    @Column()
    private int quantity = 1;

    @ManyToOne()
    private ProductModel product;
}
