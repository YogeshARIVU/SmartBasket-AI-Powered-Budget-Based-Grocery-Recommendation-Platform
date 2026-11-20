
package com.smartbasket.backend.shoppinglist.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseHistoryResponse(

        Long itemId,

        Long shoppingListId,
        String listName,

        Long customerId,
        String customerName,

        Long shopId,
        String shopName,

        Long productId,
        String productName,
        String sku,

        Integer quantity,

        BigDecimal unitPrice,
        BigDecimal totalPrice,

        LocalDateTime purchasedAt

) {
}
