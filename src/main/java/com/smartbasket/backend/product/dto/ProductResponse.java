package com.smartbasket.backend.product.dto;

import com.smartbasket.backend.product.entity.ProductUnit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(

        Long id,
        String name,
        String description,
        String sku,

        BigDecimal price,
        BigDecimal unitQuantity,
        ProductUnit unit,

        Long categoryId,
        String categoryName,

        Boolean active,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}