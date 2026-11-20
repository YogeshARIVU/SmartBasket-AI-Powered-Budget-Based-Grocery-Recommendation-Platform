
package com.smartbasket.backend.inventory.repository;

import com.smartbasket.backend.inventory.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository
        extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findAllByInventoryIdOrderByCreatedAtDesc(
            Long inventoryId
    );

    List<StockMovement> findAllByInventoryShopIdOrderByCreatedAtDesc(
            Long shopId
    );
}
