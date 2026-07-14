package com.ctdecomerce.store.retailers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDto {
        private UUID id;
        private UUID productId;
        private String productName;
        private int quantity;
        private double price;
        private String userName;
        private String email;
        private String status;
        private boolean completed;
        private int finalPriceInCents;
}
