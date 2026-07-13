package com.ctdecomerce.store.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProductDTO {
    private String name;
    private String description;
    private int priceInCents;
    private String userId;
    private List<String> categoryId;
}
