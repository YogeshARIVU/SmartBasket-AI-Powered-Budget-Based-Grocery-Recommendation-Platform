package com.smartbasket.backend.inventory.dto;

import java.time.LocalDateTime;

public record InventoryResponse(

        Long id,

        Long shopId,
        String shopName,
        String shopCode,

        Long productId,
        String productName,
        String sku,

        Integer availableQuantity,
        Integer minimumStockLevel,

        boolean lowStock,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}