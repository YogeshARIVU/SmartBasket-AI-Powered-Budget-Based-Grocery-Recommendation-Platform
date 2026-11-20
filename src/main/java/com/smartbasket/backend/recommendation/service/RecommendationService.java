
package com.smartbasket.backend.recommendation.service;

import com.smartbasket.backend.customer.entity.CustomerBudget;
import com.smartbasket.backend.customer.repository.CustomerBudgetRepository;
import com.smartbasket.backend.inventory.entity.Inventory;
import com.smartbasket.backend.inventory.repository.InventoryRepository;
import com.smartbasket.backend.product.entity.Product;
import com.smartbasket.backend.recommendation.dto.RecommendationRequest;
import com.smartbasket.backend.recommendation.dto.RecommendationResponse;
import com.smartbasket.backend.recommendation.dto.RecommendedProductResponse;
import com.smartbasket.backend.shoppinglist.entity.ShoppingListItem;
import com.smartbasket.backend.shoppinglist.repository.PurchaseHistoryRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {

    private final CustomerBudgetRepository customerBudgetRepository;
    private final InventoryRepository inventoryRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;

    public RecommendationService(
            CustomerBudgetRepository customerBudgetRepository,
            InventoryRepository inventoryRepository,
            PurchaseHistoryRepository purchaseHistoryRepository
    ) {
        this.customerBudgetRepository = customerBudgetRepository;
        this.inventoryRepository = inventoryRepository;
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }

    @Transactional(readOnly = true)
    public RecommendationResponse generateRecommendation(
            RecommendationRequest request
    ) {

        LocalDate today = LocalDate.now();

        // 1. Find the customer's current monthly budget
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

        // 2. Get customer's purchase history
        List<ShoppingListItem> purchaseHistory =
                purchaseHistoryRepository
                        .findAllByShoppingListCustomerIdAndPurchasedTrueOrderByPurchasedAtDesc(
                                request.customerId()
                        );

        Map<Long, Long> purchaseFrequency = new HashMap<>();

        for (ShoppingListItem item : purchaseHistory) {
            Long productId = item.getProduct().getId();

            purchaseFrequency.merge(productId, 1L, Long::sum);
        }

        // 3. Get available products from the selected shop
        List<Inventory> inventoryItems =
                inventoryRepository.findAllByShopId(request.shopId());

        List<Inventory> availableItems = inventoryItems.stream()
                .filter(inventory ->
                        inventory.getAvailableQuantity() != null
                                && inventory.getAvailableQuantity() > 0
                )
                .filter(inventory ->
                        Boolean.TRUE.equals(inventory.getProduct().getActive())
                )
                .filter(inventory ->
                        inventory.getProduct().getPrice() != null
                                && inventory.getProduct().getPrice()
                                .compareTo(BigDecimal.ZERO) > 0
                )
                .sorted(
                        Comparator.<Inventory>comparingLong(
                                        inventory -> purchaseFrequency.getOrDefault(
                                                inventory.getProduct().getId(), 0L
                                        )
                                )
                                .reversed()
                                .thenComparing(
                                        inventory -> inventory.getProduct().getPrice()
                                )
                )
                .toList();

        // 4. First pass: allocate one unit per eligible product
        List<ProductAllocation> allocations = new ArrayList<>();

        for (Inventory inventory : availableItems) {

            Product product = inventory.getProduct();
            BigDecimal price = product.getPrice();

            if (remainingBudget.compareTo(price) < 0) {
                continue;
            }

            ProductAllocation allocation =
                    new ProductAllocation(inventory, 1);

            allocations.add(allocation);

            remainingBudget = remainingBudget.subtract(price);
        }

        // 5. Second pass: distribute remaining budget fairly
        boolean allocationPossible = true;

        while (allocationPossible && !allocations.isEmpty()) {

            allocationPossible = false;

            for (ProductAllocation allocation : allocations) {

                Inventory inventory = allocation.inventory;
                Product product = inventory.getProduct();

                int availableQuantity =
                        inventory.getAvailableQuantity();

                BigDecimal price = product.getPrice();

                if (allocation.quantity >= availableQuantity) {
                    continue;
                }

                if (remainingBudget.compareTo(price) >= 0) {

                    allocation.quantity++;

                    remainingBudget =
                            remainingBudget.subtract(price);

                    allocationPossible = true;
                }
            }
        }

        // 6. Build recommendation response
        List<RecommendedProductResponse> recommendations =
                new ArrayList<>();

        BigDecimal totalEstimatedCost = BigDecimal.ZERO;

        for (ProductAllocation allocation : allocations) {

            Product product =
                    allocation.inventory.getProduct();

            BigDecimal price = product.getPrice();

            BigDecimal productTotal = price.multiply(
                    BigDecimal.valueOf(allocation.quantity)
            );

            recommendations.add(
                    new RecommendedProductResponse(
                            product.getId(),
                            product.getName(),
                            product.getSku(),
                            price,
                            BigDecimal.valueOf(allocation.quantity),
                            product.getUnit().name(),
                            productTotal
                    )
            );

            totalEstimatedCost =
                    totalEstimatedCost.add(productTotal);
        }

        // 7. Return final recommendation
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

    // Holds the allocated quantity for each product
    private static class ProductAllocation {

        private final Inventory inventory;
        private int quantity;

        private ProductAllocation(
                Inventory inventory,
                int quantity
        ) {
            this.inventory = inventory;
            this.quantity = quantity;
        }
    }
}
