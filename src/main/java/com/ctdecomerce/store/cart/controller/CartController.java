package com.ctdecomerce.store.cart.controller;

import com.ctdecomerce.store.cart.dto.AddToCart;
import com.ctdecomerce.store.cart.dto.UpdateQuantityRequest;
import com.ctdecomerce.store.cart.dto.UserIdRequest;
import com.ctdecomerce.store.cart.model.CartModel;
import com.ctdecomerce.store.cart.service.CartService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Setter
@AllArgsConstructor
@RestController("CartController")
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

//    public CartController(CartService cartService) {
//        this.cartService = cartService;
//    }

    @PostMapping("/add")
    public ResponseEntity<CartModel> addToCart(@RequestBody AddToCart add) {
        return new ResponseEntity<>(cartService.addToCart(add), HttpStatus.CREATED);
    }

    @PostMapping("/get")
    public ResponseEntity<List<CartModel>> getCart(@RequestBody UserIdRequest id) {
        return new ResponseEntity<>(cartService.getCart(id), HttpStatus.OK);
    }

    @PostMapping("/increment")
    public ResponseEntity<CartModel> incrementQuantity(@RequestBody UpdateQuantityRequest request) {
        return new ResponseEntity<>(cartService.incrementQuantity(request), HttpStatus.OK);
    }

    @PostMapping("/decrement")
    public ResponseEntity<CartModel> decrementQuantity(@RequestBody UpdateQuantityRequest request) {
        return new ResponseEntity<>(cartService.decrementQuantity(request), HttpStatus.OK);
    }
}
