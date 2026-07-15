package com.ctdecomerce.store.product.dto;

import lombok.*;

import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private UUID id;
    private String name;
    private OwnerDTO owner;
    private double priceInCents;
    private boolean discounted;
    private double ogPriceInCents;
    private boolean isShowing;
    private boolean isAvailable;
}
