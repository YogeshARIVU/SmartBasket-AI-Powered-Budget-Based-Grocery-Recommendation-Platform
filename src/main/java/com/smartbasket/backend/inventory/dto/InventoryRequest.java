package com.smartbasket.backend.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryRequest(

        @NotNull(message = "Shop ID is required")
        @Positive(message = "Shop ID must be greater than zero")
        Long shopId,

        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be greater than zero")
        Long productId,

        @NotNull(message = "Available quantity is required")
        @PositiveOrZero(message = "Available quantity cannot be negative")
        Integer availableQuantity,

        @NotNull(message = "Minimum stock level is required")
        @PositiveOrZero(message = "Minimum stock level cannot be negative")
        Integer minimumStockLevel

) {
}