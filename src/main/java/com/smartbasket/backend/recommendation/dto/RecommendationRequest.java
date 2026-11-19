package com.smartbasket.backend.recommendation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RecommendationRequest(

        @NotNull(message = "Customer ID is required")
        @Positive(message = "Customer ID must be greater than zero")
        Long customerId,

        @NotNull(message = "Shop ID is required")
        @Positive(message = "Shop ID must be greater than zero")
        Long shopId

) {
}