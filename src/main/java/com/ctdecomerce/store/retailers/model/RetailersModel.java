package com.ctdecomerce.store.retailers.model;

import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.user.model.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "retailers")
public class RetailersModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column()
    private String name;

    @Column(unique = true)
    private String accountId;

    @OneToOne()
    @JoinColumn()
    private UserModel user;

    @Column()
    private Date dateCreated = new Date();
}