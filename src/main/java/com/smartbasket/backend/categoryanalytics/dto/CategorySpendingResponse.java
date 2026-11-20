
package com.smartbasket.backend.categoryanalytics.dto;

import java.math.BigDecimal;

public record CategorySpendingResponse(

        Long categoryId,
        String categoryName,

        BigDecimal totalSpent,

        Long purchasedItemCount,
        Long totalPurchasedQuantity,

        BigDecimal spendingPercentage

) {
}
