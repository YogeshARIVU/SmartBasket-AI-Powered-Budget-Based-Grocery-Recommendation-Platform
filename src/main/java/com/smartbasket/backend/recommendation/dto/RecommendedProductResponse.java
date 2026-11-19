package com.smartbasket.backend.recommendation.dto;

import java.math.BigDecimal;

public record RecommendedProductResponse(

        Long productId,

        String productName,

        String sku,

        BigDecimal price,

        BigDecimal recommendedQuantity,

        String unit,

        BigDecimal totalPrice

) {
}