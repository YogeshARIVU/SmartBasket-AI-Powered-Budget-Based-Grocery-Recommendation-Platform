
package com.smartbasket.backend.shoppinglist.repository;

import com.smartbasket.backend.shoppinglist.entity.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseHistoryRepository
        extends JpaRepository<ShoppingListItem, Long> {

    // All purchased items by customer
    List<ShoppingListItem>
    findAllByShoppingListCustomerIdAndPurchasedTrueOrderByPurchasedAtDesc(
            Long customerId
    );

    // All purchased items by shop
    List<ShoppingListItem>
    findAllByShoppingListShopIdAndPurchasedTrueOrderByPurchasedAtDesc(
            Long shopId
    );

    // Purchased items by customer within a date range
    List<ShoppingListItem>
    findAllByShoppingListCustomerIdAndPurchasedTrueAndPurchasedAtBetweenOrderByPurchasedAtDesc(
            Long customerId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Purchased items by customer and shop
    List<ShoppingListItem>
    findAllByShoppingListCustomerIdAndShoppingListShopIdAndPurchasedTrueOrderByPurchasedAtDesc(
            Long customerId,
            Long shopId
    );
}
