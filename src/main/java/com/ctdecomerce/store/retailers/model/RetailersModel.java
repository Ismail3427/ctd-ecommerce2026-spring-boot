package com.ctdecomerce.store.retailers.model;

import com.ctdecomerce.store.product.model.ProductModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity(name = "retailers")
public class RetailersModel {
    @Getter
    @Setter
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
    private String accountId;

    @Getter
    @Setter
    @Column()
    private List<String> products = new ArrayList<>();

    @Getter
    @Setter
    @Column()
    private String userId;

    @Getter
    @Setter
    @Column()
    private Date dateCreated = new Date();
}