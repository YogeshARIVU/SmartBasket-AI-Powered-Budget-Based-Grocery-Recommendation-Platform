package com.smartbasket.backend.monthlyspending.dto;

import java.math.BigDecimal;

public record MonthlySpendingSummaryResponse(
        Long customerId,
        String customerName,
        Long shopId,
        String shopName,
        Integer month,
        Integer year,
        BigDecimal monthlyBudget,
        BigDecimal totalSpent,
        BigDecimal remainingBudget,
        Integer purchasedItemCount,
        Integer totalPurchasedQuantity,
        Integer distinctProductCount
) {
}