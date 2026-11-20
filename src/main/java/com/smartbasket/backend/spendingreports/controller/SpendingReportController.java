
package com.smartbasket.backend.spendingreports.controller;

import com.smartbasket.backend.spendingreports.dto.MonthlyComparisonResponse;
import com.smartbasket.backend.spendingreports.dto.MonthlySpendingReportResponse;
import com.smartbasket.backend.spendingreports.dto.SpendingTrendResponse;
import com.smartbasket.backend.spendingreports.service.SpendingReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spending-reports")
public class SpendingReportController {

    private final SpendingReportService spendingReportService;

    public SpendingReportController(
            SpendingReportService spendingReportService
    ) {
        this.spendingReportService = spendingReportService;
    }

    // 1. Get monthly spending report
    @GetMapping("/customer/{customerId}/shop/{shopId}/monthly")
    public ResponseEntity<MonthlySpendingReportResponse>
    getMonthlyReport(

            @PathVariable Long customerId,
            @PathVariable Long shopId,
            @RequestParam Integer month,
            @RequestParam Integer year

    ) {

        return ResponseEntity.ok(
                spendingReportService.getMonthlyReport(
                        customerId,
                        shopId,
                        month,
                        year
                )
        );
    }

    // 2. Compare current month with previous month
    @GetMapping("/customer/{customerId}/shop/{shopId}/comparison")
    public ResponseEntity<MonthlyComparisonResponse>
    compareMonthlySpending(

            @PathVariable Long customerId,
            @PathVariable Long shopId,
            @RequestParam Integer month,
            @RequestParam Integer year

    ) {

        return ResponseEntity.ok(
                spendingReportService.compareMonthlySpending(
                        customerId,
                        shopId,
                        month,
                        year
                )
        );
    }

    // 3. Get spending trend
    @GetMapping("/customer/{customerId}/shop/{shopId}/trend")
    public ResponseEntity<SpendingTrendResponse>
    getSpendingTrend(

            @PathVariable Long customerId,
            @PathVariable Long shopId,

            @RequestParam(defaultValue = "6")
            Integer numberOfMonths

    ) {

        return ResponseEntity.ok(
                spendingReportService.getSpendingTrend(
                        customerId,
                        shopId,
                        numberOfMonths
                )
        );
    }
}
