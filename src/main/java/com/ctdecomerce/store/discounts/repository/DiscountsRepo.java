package com.ctdecomerce.store.discounts.repository;

import com.ctdecomerce.store.discounts.model.DiscountsModel;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.retailers.model.RetailersModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DiscountsRepo extends JpaRepository<DiscountsModel, UUID> {
    DiscountsModel findDiscountsModelByProduct(ProductModel product);
    List<DiscountsModel> findDiscountsModelsByRetailer(RetailersModel retailer);
}
