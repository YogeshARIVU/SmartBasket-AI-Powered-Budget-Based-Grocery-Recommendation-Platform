
package com.smartbasket.backend.shoppinglist.controller;

import com.smartbasket.backend.shoppinglist.dto.PurchaseHistoryResponse;
import com.smartbasket.backend.shoppinglist.service.PurchaseHistoryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-history")
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;

    public PurchaseHistoryController(
            PurchaseHistoryService purchaseHistoryService
    ) {
        this.purchaseHistoryService = purchaseHistoryService;
    }

    // 1. Get all purchases by customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PurchaseHistoryResponse>>
    getCustomerPurchaseHistory(
            @PathVariable Long customerId
    ) {
        return ResponseEntity.ok(
                purchaseHistoryService.getCustomerPurchaseHistory(customerId)
        );
    }

    // 2. Get all purchases at a shop
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<PurchaseHistoryResponse>>
    getShopPurchaseHistory(
            @PathVariable Long shopId
    ) {
        return ResponseEntity.ok(
                purchaseHistoryService.getShopPurchaseHistory(shopId)
        );
    }

    // 3. Get customer purchases within a date range
    @GetMapping("/customer/{customerId}/date-range")
    public ResponseEntity<List<PurchaseHistoryResponse>>
    getCustomerPurchaseHistoryByDateRange(

            @PathVariable Long customerId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate

    ) {
        return ResponseEntity.ok(
                purchaseHistoryService.getCustomerPurchaseHistoryByDateRange(
                        customerId,
                        startDate,
                        endDate
                )
        );
    }

    // 4. Get customer purchases from a specific shop
    @GetMapping("/customer/{customerId}/shop/{shopId}")
    public ResponseEntity<List<PurchaseHistoryResponse>>
    getCustomerPurchaseHistoryByShop(

            @PathVariable Long customerId,
            @PathVariable Long shopId

    ) {
        return ResponseEntity.ok(
                purchaseHistoryService.getCustomerPurchaseHistoryByShop(
                        customerId,
                        shopId
                )
        );
    }
}
