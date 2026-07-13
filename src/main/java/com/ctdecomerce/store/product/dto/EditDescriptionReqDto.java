package com.ctdecomerce.store.product.dto;


import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditDescriptionReqDto {
    private UUID product_id;
    private String description;
}
