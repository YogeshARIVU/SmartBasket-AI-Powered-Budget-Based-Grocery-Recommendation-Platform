
package com.smartbasket.backend.categoryanalytics.repository;

import com.smartbasket.backend.shoppinglist.entity.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CategoryAnalyticsRepository
        extends JpaRepository<ShoppingListItem, Long> {

    interface CategorySpendingProjection {

        Long getCategoryId();

        String getCategoryName();

        BigDecimal getTotalSpent();

        Long getPurchasedItemCount();

        Long getTotalPurchasedQuantity();
    }

    @Query("""
            SELECT
                category.id AS categoryId,
                category.name AS categoryName,
                SUM(item.unitPrice * item.quantity) AS totalSpent,
                COUNT(item.id) AS purchasedItemCount,
                SUM(item.quantity) AS totalPurchasedQuantity
            FROM ShoppingListItem item
            JOIN item.shoppingList list
            JOIN item.product product
            JOIN product.category category
            WHERE list.customer.id = :customerId
            AND list.shop.id = :shopId
            AND item.purchased = true
            AND item.purchasedAt >= :startDate
            AND item.purchasedAt < :endDate
            GROUP BY category.id, category.name
            ORDER BY SUM(item.unitPrice * item.quantity) DESC
            """)
    List<CategorySpendingProjection> findCategorySpending(

            @Param("customerId") Long customerId,

            @Param("shopId") Long shopId,

            @Param("startDate") LocalDateTime startDate,

            @Param("endDate") LocalDateTime endDate

    );
}
