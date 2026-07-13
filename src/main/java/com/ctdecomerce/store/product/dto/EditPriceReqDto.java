package com.ctdecomerce.store.product.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditPriceReqDto {
    private UUID product_id;
    private int priceInCents;
}
