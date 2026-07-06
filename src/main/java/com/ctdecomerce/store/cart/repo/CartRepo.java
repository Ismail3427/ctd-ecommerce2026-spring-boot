package com.ctdecomerce.store.cart.repo;

import com.ctdecomerce.store.cart.model.CartModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartRepo extends JpaRepository<CartModel, UUID> {
    List<CartModel> findCartMOdelByUserId(String userId);
}
