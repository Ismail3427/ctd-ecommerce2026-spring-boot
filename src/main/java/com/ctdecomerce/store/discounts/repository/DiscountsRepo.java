package com.ctdecomerce.store.discounts.repository;

import com.ctdecomerce.store.discounts.model.DiscountsModel;
import com.ctdecomerce.store.product.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DiscountsRepo extends JpaRepository<DiscountsModel, UUID> {
    List<ProductModel> findDiscountsModelsByProduct(ProductModel product);
}
