package com.ctdecomerce.store.product.repository;

import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.retailers.dto.RetailerIdRequest;
import com.ctdecomerce.store.retailers.model.RetailersModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepo extends JpaRepository<ProductModel, UUID> {
    List<ProductModel> findProductModelsByOwner(RetailersModel retailer);
}
