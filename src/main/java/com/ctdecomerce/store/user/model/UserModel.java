package com.ctdecomerce.store.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    private UUID id;

    @Column(name="user_id",unique = true)
    private String userId;

    @Column(name="email")
    private String email;

    @Column(name="ip_address")
    private String ipAddress;

    @Column()
    private String name;

    @Column(name="logins_count")
    private int loginsCount;
}

