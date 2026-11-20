
package com.smartbasket.backend.categoryanalytics.controller;

import com.smartbasket.backend.categoryanalytics.dto.CategorySpendingSummaryResponse;
import com.smartbasket.backend.categoryanalytics.service.CategoryAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category-analytics")
public class CategoryAnalyticsController {

    private final CategoryAnalyticsService categoryAnalyticsService;

    public CategoryAnalyticsController(
            CategoryAnalyticsService categoryAnalyticsService
    ) {
        this.categoryAnalyticsService = categoryAnalyticsService;
    }

    // Get monthly category-wise spending
    @GetMapping("/customer/{customerId}/shop/{shopId}")
    public ResponseEntity<CategorySpendingSummaryResponse>
    getCategorySpending(

            @PathVariable Long customerId,

            @PathVariable Long shopId,

            @RequestParam Integer month,

            @RequestParam Integer year

    ) {

        CategorySpendingSummaryResponse response =
                categoryAnalyticsService.getCategorySpending(
                        customerId,
                        shopId,
                        month,
                        year
                );

        return ResponseEntity.ok(response);
    }
}
