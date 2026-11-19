
package com.smartbasket.backend.inventory.controller;

import com.smartbasket.backend.inventory.dto.InventoryRequest;
import com.smartbasket.backend.inventory.dto.InventoryResponse;
import com.smartbasket.backend.inventory.dto.StockUpdateRequest;
import com.smartbasket.backend.inventory.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public InventoryResponse createInventory(
            @Valid @RequestBody InventoryRequest request
    ) {
        return inventoryService.createInventory(request);
    }

    @GetMapping
    public List<InventoryResponse> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @GetMapping("/low-stock")
    public List<InventoryResponse> getLowStockItems() {
        return inventoryService.getLowStockItems();
    }

    @GetMapping("/{id}")
    public InventoryResponse getInventoryById(
            @PathVariable @Positive Long id
    ) {
        return inventoryService.getInventoryById(id);
    }

    @GetMapping("/shop/{shopId}")
    public List<InventoryResponse> getInventoryByShopId(
            @PathVariable @Positive Long shopId
    ) {
        return inventoryService.getInventoryByShopId(shopId);
    }

    @GetMapping("/shop/{shopId}/low-stock")
    public List<InventoryResponse> getLowStockItemsByShopId(
            @PathVariable @Positive Long shopId
    ) {
        return inventoryService.getLowStockItemsByShopId(shopId);
    }

    @GetMapping("/shop/{shopId}/product/{productId}")
    public InventoryResponse getInventoryByShopAndProduct(
            @PathVariable @Positive Long shopId,
            @PathVariable @Positive Long productId
    ) {
        return inventoryService.getInventoryByShopAndProduct(
                shopId,
                productId
        );
    }

    @PatchMapping("/{id}/stock")
    public InventoryResponse updateStock(
            @PathVariable @Positive Long id,
            @Valid @RequestBody StockUpdateRequest request
    ) {
        return inventoryService.updateStock(id, request);
    }

    @PatchMapping("/{id}/minimum-stock")
    public InventoryResponse updateMinimumStockLevel(
            @PathVariable @Positive Long id,
            @RequestParam @Positive Integer value
    ) {
        return inventoryService.updateMinimumStockLevel(id, value);
    }
}
