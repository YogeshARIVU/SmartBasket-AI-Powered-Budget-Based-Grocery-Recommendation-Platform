package com.smartbasket.backend.shoppinglist.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ShoppingListResponse(

        Long id,

        Long customerId,
        String customerName,

        Long shopId,
        String shopName,

        String listName,

        BigDecimal totalEstimatedCost,
        BigDecimal purchasedCost,
        BigDecimal pendingCost,
        BigDecimal remainingBudget,

        List<ShoppingListItemResponse> items,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}