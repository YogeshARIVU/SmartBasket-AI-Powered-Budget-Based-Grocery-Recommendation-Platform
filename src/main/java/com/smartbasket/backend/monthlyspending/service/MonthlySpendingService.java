
package com.smartbasket.backend.monthlyspending.service;

import com.smartbasket.backend.customer.entity.Customer;
import com.smartbasket.backend.customer.entity.CustomerBudget;
import com.smartbasket.backend.customer.repository.CustomerBudgetRepository;
import com.smartbasket.backend.customer.repository.CustomerRepository;
import com.smartbasket.backend.monthlyspending.dto.MonthlySpendingSummaryResponse;
import com.smartbasket.backend.monthlyspending.repository.MonthlySpendingRepository;
import com.smartbasket.backend.shop.entity.Shop;
import com.smartbasket.backend.shop.repository.ShopRepository;
import com.smartbasket.backend.shoppinglist.entity.ShoppingListItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class MonthlySpendingService {

    private final MonthlySpendingRepository monthlySpendingRepository;
    private final CustomerRepository customerRepository;
    private final ShopRepository shopRepository;
    private final CustomerBudgetRepository customerBudgetRepository;

    public MonthlySpendingService(
            MonthlySpendingRepository monthlySpendingRepository,
            CustomerRepository customerRepository,
            ShopRepository shopRepository,
            CustomerBudgetRepository customerBudgetRepository
    ) {
        this.monthlySpendingRepository = monthlySpendingRepository;
        this.customerRepository = customerRepository;
        this.shopRepository = shopRepository;
        this.customerBudgetRepository = customerBudgetRepository;
    }

    public MonthlySpendingSummaryResponse getMonthlySpendingSummary(
            Long customerId,
            Long shopId,
            Integer month,
            Integer year
    ) {

        // Validate month and year
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12"
            );
        }

        if (year == null || year < 1) {
            throw new IllegalArgumentException(
                    "Year must be valid"
            );
        }

        // Find customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found with ID: " + customerId
                ));

        // Find shop
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Shop not found with ID: " + shopId
                ));

        // Find customer's monthly budget for this shop
        CustomerBudget budget = customerBudgetRepository
                .findByCustomerIdAndShopIdAndMonthAndYear(
                        customerId,
                        shopId,
                        month,
                        year
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Monthly budget not found for this customer and shop"
                ));

        // Calculate month date range
        LocalDate firstDay = LocalDate.of(year, month, 1);

        LocalDateTime startDate = firstDay.atStartOfDay();

        LocalDateTime endDate = firstDay
                .plusMonths(1)
                .atStartOfDay();

        // Retrieve purchased items for the selected month
        List<ShoppingListItem> purchasedItems =
                monthlySpendingRepository.findMonthlyPurchasedItems(
                        customerId,
                        shopId,
                        startDate,
                        endDate
                );

        // Calculate total spending
        BigDecimal totalSpent = BigDecimal.ZERO;

        int totalPurchasedQuantity = 0;

        Set<Long> distinctProducts = new HashSet<>();

        for (ShoppingListItem item : purchasedItems) {

            totalSpent = totalSpent.add(item.getTotalPrice());

            totalPurchasedQuantity += item.getQuantity();

            distinctProducts.add(item.getProduct().getId());
        }

        // Calculate remaining budget
        BigDecimal remainingBudget =
                budget.getMonthlyBudget().subtract(totalSpent);

        // Return monthly spending summary
        return new MonthlySpendingSummaryResponse(

                customer.getId(),
                customer.getFullName(),

                shop.getId(),
                shop.getShopName(),

                month,
                year,

                budget.getMonthlyBudget(),
                totalSpent,
                remainingBudget,

                purchasedItems.size(),
                totalPurchasedQuantity,
                distinctProducts.size()

        );
    }
}
