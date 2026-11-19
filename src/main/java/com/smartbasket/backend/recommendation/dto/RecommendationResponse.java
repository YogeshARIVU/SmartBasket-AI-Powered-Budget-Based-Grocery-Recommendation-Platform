package com.smartbasket.backend.recommendation.dto;

import java.math.BigDecimal;
import java.util.List;

public record RecommendationResponse(

        Long customerId,
        Long shopId,
        String shopName,

        BigDecimal monthlyBudget,
        BigDecimal spentAmount,
        BigDecimal totalEstimatedCost,
        BigDecimal remainingBudget,

        List<RecommendedProductResponse> recommendedProducts

) {
}