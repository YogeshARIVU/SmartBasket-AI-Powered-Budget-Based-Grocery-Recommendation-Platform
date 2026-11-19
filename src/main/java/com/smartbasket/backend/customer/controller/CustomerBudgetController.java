
package com.smartbasket.backend.customer.controller;

import com.smartbasket.backend.customer.dto.BudgetRequest;
import com.smartbasket.backend.customer.dto.BudgetResponse;
import com.smartbasket.backend.customer.service.CustomerBudgetService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class CustomerBudgetController {

    private final CustomerBudgetService budgetService;

    public CustomerBudgetController(
            CustomerBudgetService budgetService) {

        this.budgetService = budgetService;
    }

    // Create Budget
    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @Valid @RequestBody BudgetRequest request) {

        BudgetResponse response =
                budgetService.createBudget(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get All Budgets
    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets() {

        return ResponseEntity.ok(
                budgetService.getAllBudgets()
        );
    }

    // Get Budget By ID
    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudgetById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                budgetService.getBudgetById(id)
        );
    }

    // Get Budgets By Customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                budgetService.getBudgetsByCustomer(customerId)
        );
    }

    // Get Budgets By Shop
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByShop(
            @PathVariable Long shopId) {

        return ResponseEntity.ok(
                budgetService.getBudgetsByShop(shopId)
        );
    }

    // Get Customer Budgets By Month And Year
    @GetMapping("/customer/{customerId}/period")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByPeriod(
            @PathVariable Long customerId,
            @RequestParam Integer month,
            @RequestParam Integer year) {

        return ResponseEntity.ok(
                budgetService.getBudgetsByCustomerAndPeriod(
                        customerId,
                        month,
                        year
                )
        );
    }

    // Update Budget
    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {

        return ResponseEntity.ok(
                budgetService.updateBudget(id, request)
        );
    }

    // Delete Budget
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long id) {

        budgetService.deleteBudget(id);

        return ResponseEntity.noContent().build();
    }
}
