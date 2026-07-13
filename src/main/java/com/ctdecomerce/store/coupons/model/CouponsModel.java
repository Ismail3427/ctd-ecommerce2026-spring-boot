package com.ctdecomerce.store.coupons.model;

import jakarta.persistence.*;
import lombok.*;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "coupons")
public class CouponsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column()
    private String code;

    @PrePersist
    private void generateCode() {
        if (this.code == null || this.code.isEmpty()) {
            Random random = new SecureRandom();
            this.code = String.format("%06d", random.nextInt(1000000));
        }
    }

    @Column()
    private double offer;
}
