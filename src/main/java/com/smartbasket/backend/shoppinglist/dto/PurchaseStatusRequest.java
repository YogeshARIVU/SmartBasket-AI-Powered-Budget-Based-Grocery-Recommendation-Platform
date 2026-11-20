package com.smartbasket.backend.shoppinglist.dto;

import jakarta.validation.constraints.NotNull;

public record PurchaseStatusRequest(

        @NotNull(message = "Purchased status is required")
        Boolean purchased
) {
}