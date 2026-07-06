package com.ctdecomerce.store.cart.service;

import com.ctdecomerce.store.cart.model.CartModel;
import com.ctdecomerce.store.cart.repo.CartRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final CartRepo cartRepo;

    public CartService(CartRepo cartRepo) {
        this.cartRepo = cartRepo;
    }

    @Transactional
    public CartModel addToCart() {}
}
