package com.smartbasket.backend.recommendation.service;

import com.smartbasket.backend.customer.entity.CustomerBudget;
import com.smartbasket.backend.customer.repository.CustomerBudgetRepository;
import com.smartbasket.backend.inventory.entity.Inventory;
import com.smartbasket.backend.inventory.repository.InventoryRepository;
import com.smartbasket.backend.product.entity.Product;
import com.smartbasket.backend.recommendation.dto.RecommendationRequest;
import com.smartbasket.backend.recommendation.dto.RecommendationResponse;
import com.smartbasket.backend.recommendation.dto.RecommendedProductResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RecommendationService {

    private final CustomerBudgetRepository customerBudgetRepository;
    private final InventoryRepository inventoryRepository;

    public RecommendationService(
            CustomerBudgetRepository customerBudgetRepository,
            InventoryRepository inventoryRepository
    ) {
        this.customerBudgetRepository = customerBudgetRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public RecommendationResponse generateRecommendation(
            RecommendationRequest request
    ) {

        LocalDate today = LocalDate.now();

        // 1. Find customer's monthly budget for this shop
        CustomerBudget budget = customerBudgetRepository
                .findByCustomerIdAndShopIdAndMonthAndYear(
                        request.customerId(),
                        request.shopId(),
                        today.getMonthValue(),
                        today.getYear()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Monthly budget not found for this customer and shop"
                ));

        // 2. Calculate remaining budget
        BigDecimal monthlyBudget = budget.getMonthlyBudget();

        BigDecimal spentAmount = budget.getSpentAmount() == null
                ? BigDecimal.ZERO
                : budget.getSpentAmount();

        BigDecimal remainingBudget =
                monthlyBudget.subtract(spentAmount);

        if (remainingBudget.compareTo(BigDecimal.ZERO) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer has no remaining budget"
            );
        }

        // 3. Get inventory belonging to the selected shop
        List<Inventory> inventoryItems =
                inventoryRepository.findAllByShopId(request.shopId());

        // 4. Keep active products that have available stock
        List<Inventory> availableItems = inventoryItems.stream()
                .filter(inventory ->
                        inventory.getAvailableQuantity() != null
                                && inventory.getAvailableQuantity() > 0
                )
                .filter(inventory ->
                        Boolean.TRUE.equals(
                                inventory.getProduct().getActive()
                        )
                )
                .filter(inventory ->
                        inventory.getProduct().getPrice() != null
                                && inventory.getProduct().getPrice()
                                .compareTo(BigDecimal.ZERO) > 0
                )
                .sorted(Comparator.comparing(
                        inventory -> inventory.getProduct().getPrice()
                ))
                .toList();

        // 5. Generate recommendations
        List<RecommendedProductResponse> recommendations =
                new ArrayList<>();

        BigDecimal totalEstimatedCost = BigDecimal.ZERO;

        for (Inventory inventory : availableItems) {

            Product product = inventory.getProduct();

            BigDecimal price = product.getPrice();

            int availableQuantity = inventory.getAvailableQuantity();

            // Calculate how many units the customer can afford
            int affordableQuantity = remainingBudget
                    .divide(price, 0, RoundingMode.DOWN)
                    .intValue();

            // Never recommend more than available stock
            int recommendedQuantity = Math.min(
                    affordableQuantity,
                    availableQuantity
            );

            if (recommendedQuantity <= 0) {
                continue;
            }

            // Calculate product total
            BigDecimal productTotal = price.multiply(
                    BigDecimal.valueOf(recommendedQuantity)
            );

            // Add recommended product
            recommendations.add(
                    new RecommendedProductResponse(
                            product.getId(),
                            product.getName(),
                            product.getSku(),
                            price,
                            BigDecimal.valueOf(recommendedQuantity),
                            product.getUnit().name(),
                            productTotal
                    )
            );

            // Update totals
            totalEstimatedCost =
                    totalEstimatedCost.add(productTotal);

            remainingBudget =
                    remainingBudget.subtract(productTotal);
        }

        // 6. Return recommendation response
        return new RecommendationResponse(
                budget.getCustomer().getId(),
                budget.getShop().getId(),
                budget.getShop().getShopName(),
                monthlyBudget,
                spentAmount,
                totalEstimatedCost,
                remainingBudget,
                recommendations
        );
    }
}