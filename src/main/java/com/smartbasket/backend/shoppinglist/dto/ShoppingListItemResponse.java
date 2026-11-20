package com.smartbasket.backend.shoppinglist.dto;

import java.math.BigDecimal;

public record ShoppingListItemResponse(
        Long itemId,
        Long productId,
        String productName,
        String sku,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        Boolean purchased
) {
}