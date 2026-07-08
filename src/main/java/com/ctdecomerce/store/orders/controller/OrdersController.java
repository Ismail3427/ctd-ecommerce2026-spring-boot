package com.ctdecomerce.store.orders.controller;

import com.ctdecomerce.store.orders.dto.UserRequest;
import com.ctdecomerce.store.orders.model.OrdersModel;
import com.ctdecomerce.store.orders.service.OrdersService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("OrdersController")
@RequestMapping("/orders")
@Setter
@AllArgsConstructor
public class OrdersController {
    private final OrdersService ordersService;

    @PostMapping("/get")
    public ResponseEntity<List<OrdersModel>> getOrders(@RequestBody UserRequest userRequest) {
        return new ResponseEntity<>(ordersService.getAllOrdersForUser(userRequest), HttpStatus.OK);
    }
}
