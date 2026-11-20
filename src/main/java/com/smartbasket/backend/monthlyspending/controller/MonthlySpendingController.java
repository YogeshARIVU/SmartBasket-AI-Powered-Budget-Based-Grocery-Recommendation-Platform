
package com.smartbasket.backend.monthlyspending.controller;

import com.smartbasket.backend.monthlyspending.dto.MonthlySpendingSummaryResponse;
import com.smartbasket.backend.monthlyspending.service.MonthlySpendingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/monthly-spending")
public class MonthlySpendingController {

    private final MonthlySpendingService monthlySpendingService;

    public MonthlySpendingController(
            MonthlySpendingService monthlySpendingService
    ) {
        this.monthlySpendingService = monthlySpendingService;
    }

    // Get monthly spending summary for a customer at a shop
    @GetMapping("/customer/{customerId}/shop/{shopId}")
    public ResponseEntity<MonthlySpendingSummaryResponse>
    getMonthlySpendingSummary(

            @PathVariable Long customerId,

            @PathVariable Long shopId,

            @RequestParam Integer month,

            @RequestParam Integer year

    ) {

        MonthlySpendingSummaryResponse response =
                monthlySpendingService.getMonthlySpendingSummary(
                        customerId,
                        shopId,
                        month,
                        year
                );

        return ResponseEntity.ok(response);
    }
}
