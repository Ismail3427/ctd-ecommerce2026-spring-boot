package com.ctdecomerce.store.discounts.service;

import com.ctdecomerce.store.discounts.dto.FindByProduct;
import com.ctdecomerce.store.discounts.model.DiscountsModel;
import com.ctdecomerce.store.discounts.repository.DiscountsRepo;
import com.ctdecomerce.store.product.dto.ProductDTO;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.product.repository.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Setter
@AllArgsConstructor
@Service
public class DiscountsService {
    private final DiscountsRepo discountsRepo;
    private final ProductRepo productRepo;

    @Transactional
    public List<ProductModel> getDiscountsByProduct(FindByProduct productStrings) {
        UUID productIdAsUUID = UUID.fromString(productStrings.getProductId());
        ProductModel product = productRepo.findById(productIdAsUUID).orElse(null);
        return discountsRepo.findDiscountsModelsByProduct(product);
    }
}
