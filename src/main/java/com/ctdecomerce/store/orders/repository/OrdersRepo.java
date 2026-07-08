package com.ctdecomerce.store.orders.repository;

import com.ctdecomerce.store.orders.model.OrdersModel;
import com.ctdecomerce.store.user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrdersRepo extends JpaRepository<OrdersModel, UUID> {
    List<OrdersModel> findOrdersModelsByUser(UserModel user);
}
