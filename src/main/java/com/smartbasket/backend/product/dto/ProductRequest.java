
package com.smartbasket.backend.product.dto;

import com.smartbasket.backend.product.entity.ProductUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "Product name is required")
        @Size(max = 150, message = "Product name cannot exceed 150 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotBlank(message = "SKU is required")
        @Size(max = 50, message = "SKU cannot exceed 50 characters")
        String sku,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,

        @NotNull(message = "Unit quantity is required")
        @Positive(message = "Unit quantity must be greater than zero")
        BigDecimal unitQuantity,

        @NotNull(message = "Product unit is required")
        ProductUnit unit,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be greater than zero")
        Long categoryId,

        @NotNull(message = "Active status is required")
        Boolean active

) {
}
