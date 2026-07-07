package com.ctdecomerce.store.user.repository;

import com.ctdecomerce.store.user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepo extends JpaRepository<UserModel, UUID> {
}
