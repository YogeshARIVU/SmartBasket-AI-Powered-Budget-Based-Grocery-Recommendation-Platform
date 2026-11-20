
package com.smartbasket.backend.spendingreports.dto;

import java.math.BigDecimal;

public record MonthlySpendingReportResponse(

        Long customerId,
        String customerName,

        Long shopId,
        String shopName,

        Integer month,
        Integer year,

        BigDecimal monthlyBudget,
        BigDecimal totalSpent,
        BigDecimal remainingBudget,
        BigDecimal budgetUtilizationPercentage,

        Long purchasedItemCount,
        Long totalPurchasedQuantity

) {
}
