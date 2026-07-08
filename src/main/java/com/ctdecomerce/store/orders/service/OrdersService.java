package com.ctdecomerce.store.orders.service;

import com.ctdecomerce.store.orders.dto.UserRequest;
import com.ctdecomerce.store.orders.model.OrdersModel;
import com.ctdecomerce.store.orders.repository.OrdersRepo;
import com.ctdecomerce.store.user.model.UserModel;
import com.ctdecomerce.store.user.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Setter
@AllArgsConstructor
public class OrdersService {
    private final OrdersRepo ordersRepo;
    private final UserRepo userRepo;

    @Transactional
    public List<OrdersModel> getAllOrdersForUser(UserRequest userRequest) {
        UserModel user = userRepo.findUserModelByUserId(userRequest.getUserId());
        return ordersRepo.findOrdersModelsByUser(user);
    }
}
