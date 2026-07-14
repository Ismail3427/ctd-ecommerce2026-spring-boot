package com.ctdecomerce.store.discounts.model;

import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.retailers.model.RetailersModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "discounts")
public class DiscountsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne()
    @JoinColumn()
    private ProductModel product;

    @Column()
    private double offer;

    @Column()
    private String name;

    @ManyToOne()
    @JoinColumn()
    private RetailersModel retailer;
}
