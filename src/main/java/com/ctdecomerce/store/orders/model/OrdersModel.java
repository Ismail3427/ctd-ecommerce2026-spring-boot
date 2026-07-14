package com.ctdecomerce.store.orders.model;

import com.ctdecomerce.store.cart.model.CartModel;
import com.ctdecomerce.store.user.model.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "orders")
public class OrdersModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne()
    @JoinColumn()
    private UserModel user;

    @Column()
    private double finalPriceInCents;

    @OneToOne()
    @JoinTable(
            name = "orders_cart",
            joinColumns = @JoinColumn(name = "orders_id"),
            inverseJoinColumns = @JoinColumn(name = "cart_id")
    )
    private CartModel cart;

    @Column()
    private boolean completed = false;

    @Column()
    private String status = "pending";

    @Column()
    private Date timestamp = new Date();
}