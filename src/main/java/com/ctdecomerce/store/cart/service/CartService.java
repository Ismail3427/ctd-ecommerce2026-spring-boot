package com.ctdecomerce.store.cart.service;

import com.ctdecomerce.store.cart.dto.AddToCart;
import com.ctdecomerce.store.cart.dto.UpdateQuantityRequest;
import com.ctdecomerce.store.cart.dto.UserIdRequest;
import com.ctdecomerce.store.cart.model.CartModel;
import com.ctdecomerce.store.cart.repo.CartRepo;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.product.repository.ProductRepo;
import com.stripe.model.Product;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Setter
@AllArgsConstructor
@Service
public class CartService {
    private final CartRepo cartRepo;
    private final ProductRepo productRepo;



    @Transactional
    public CartModel addToCart(AddToCart addToCart) {
        CartModel cart = new CartModel();
        cart.setUserId(addToCart.getUserId());
        ProductModel product = productRepo.findById(UUID.fromString(addToCart.getProductId())).orElse(null);
        System.out.println(product);
        cart.setProduct(product);
        return cartRepo.save(cart);
    }

    @Transactional
    public List<CartModel> getCart(UserIdRequest userIdRequest) {
        return cartRepo.findCartModelsByUserId(userIdRequest.getUserId());
    }

    @Transactional
    public CartModel incrementQuantity(UpdateQuantityRequest request) {
        CartModel cart = cartRepo.findById(UUID.fromString(request.getCartId()))
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.setQuantity(cart.getQuantity() + 1);
        return cartRepo.save(cart);
    }

    @Transactional
    public CartModel decrementQuantity(UpdateQuantityRequest request) {
        CartModel cart = cartRepo.findById(UUID.fromString(request.getCartId()))
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        if (cart.getQuantity() > 1) {
            cart.setQuantity(cart.getQuantity() - 1);
        }
        return cartRepo.save(cart);
    }

}