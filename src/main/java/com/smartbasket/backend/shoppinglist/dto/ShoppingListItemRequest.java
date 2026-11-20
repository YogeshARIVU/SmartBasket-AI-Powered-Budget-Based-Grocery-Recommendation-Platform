package com.smartbasket.backend.shoppinglist.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ShoppingListItemRequest(

        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be greater than zero")
        Long productId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than zero")
        Integer quantity
) {
}