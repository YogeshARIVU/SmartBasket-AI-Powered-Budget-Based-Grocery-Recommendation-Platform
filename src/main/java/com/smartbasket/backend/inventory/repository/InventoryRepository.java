
package com.smartbasket.backend.inventory.repository;

import com.smartbasket.backend.inventory.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository
        extends JpaRepository<Inventory, Long> {

    boolean existsByShopIdAndProductId(
            Long shopId,
            Long productId
    );

    Optional<Inventory> findByShopIdAndProductId(
            Long shopId,
            Long productId
    );

    List<Inventory> findAllByShopId(Long shopId);

    @Query("""
        SELECT i FROM Inventory i
        WHERE i.availableQuantity <= i.minimumStockLevel
    """)
    List<Inventory> findLowStockItems();

    @Query("""
        SELECT i FROM Inventory i
        WHERE i.shop.id = :shopId
        AND i.availableQuantity <= i.minimumStockLevel
    """)
    List<Inventory> findLowStockItemsByShopId(
            @Param("shopId") Long shopId
    );

    boolean existsByProductId(Long productId);

    Optional<Inventory> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT i FROM Inventory i
        WHERE i.shop.id = :shopId
        AND i.product.id = :productId
    """)
    Optional<Inventory> findByShopIdAndProductIdForUpdate(
            @Param("shopId") Long shopId,
            @Param("productId") Long productId
    );
}
