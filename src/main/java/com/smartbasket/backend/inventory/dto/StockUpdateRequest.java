
package com.smartbasket.backend.inventory.dto;

import com.smartbasket.backend.inventory.enums.StockMovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockUpdateRequest(

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than zero")
        Integer quantity,

        @NotNull(message = "Movement type is required")
        StockMovementType movementType

) {
}
