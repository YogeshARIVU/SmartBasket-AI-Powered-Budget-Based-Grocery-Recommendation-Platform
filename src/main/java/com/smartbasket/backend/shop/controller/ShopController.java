
package com.smartbasket.backend.shop.controller;

import com.smartbasket.backend.shop.dto.ShopRequest;
import com.smartbasket.backend.shop.dto.ShopResponse;
import com.smartbasket.backend.shop.service.ShopService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    // Create Shop
    @PostMapping
    public ResponseEntity<ShopResponse> createShop(
            @Valid @RequestBody ShopRequest request) {

        ShopResponse response = shopService.createShop(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get All Shops
    @GetMapping
    public ResponseEntity<List<ShopResponse>> getAllShops() {

        return ResponseEntity.ok(shopService.getAllShops());
    }

    // Get Shop By ID
    @GetMapping("/{id}")
    public ResponseEntity<ShopResponse> getShopById(
            @PathVariable Long id) {

        return ResponseEntity.ok(shopService.getShopById(id));
    }

    // Update Shop
    @PutMapping("/{id}")
    public ResponseEntity<ShopResponse> updateShop(
            @PathVariable Long id,
            @Valid @RequestBody ShopRequest request) {

        return ResponseEntity.ok(
                shopService.updateShop(id, request)
        );
    }

    // Delete Shop
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(
            @PathVariable Long id) {

        shopService.deleteShop(id);

        return ResponseEntity.noContent().build();
    }

    // Search Shops
    @GetMapping("/search")
    public ResponseEntity<List<ShopResponse>> searchShops(
            @RequestParam String name) {

        return ResponseEntity.ok(shopService.searchShops(name));
    }
}
