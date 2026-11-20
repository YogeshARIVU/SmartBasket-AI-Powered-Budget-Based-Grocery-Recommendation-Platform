
package com.smartbasket.backend.monthlyspending.repository;

import com.smartbasket.backend.shoppinglist.entity.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MonthlySpendingRepository
        extends JpaRepository<ShoppingListItem, Long> {

    @Query("""
            SELECT item
            FROM ShoppingListItem item
            WHERE item.shoppingList.customer.id = :customerId
            AND item.shoppingList.shop.id = :shopId
            AND item.purchased = true
            AND item.purchasedAt >= :startDate
            AND item.purchasedAt < :endDate
            ORDER BY item.purchasedAt DESC
            """)
    List<ShoppingListItem> findMonthlyPurchasedItems(

            @Param("customerId") Long customerId,

            @Param("shopId") Long shopId,

            @Param("startDate") LocalDateTime startDate,

            @Param("endDate") LocalDateTime endDate

    );
}
