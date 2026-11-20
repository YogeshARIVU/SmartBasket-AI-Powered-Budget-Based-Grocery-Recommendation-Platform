
package com.smartbasket.backend.categoryanalytics.service;

import com.smartbasket.backend.categoryanalytics.dto.CategorySpendingResponse;
import com.smartbasket.backend.categoryanalytics.dto.CategorySpendingSummaryResponse;
import com.smartbasket.backend.categoryanalytics.repository.CategoryAnalyticsRepository;
import com.smartbasket.backend.categoryanalytics.repository.CategoryAnalyticsRepository.CategorySpendingProjection;
import com.smartbasket.backend.customer.entity.Customer;
import com.smartbasket.backend.customer.repository.CustomerRepository;
import com.smartbasket.backend.shop.entity.Shop;
import com.smartbasket.backend.shop.repository.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryAnalyticsService {

    private final CategoryAnalyticsRepository categoryAnalyticsRepository;
    private final CustomerRepository customerRepository;
    private final ShopRepository shopRepository;

    public CategoryAnalyticsService(
            CategoryAnalyticsRepository categoryAnalyticsRepository,
            CustomerRepository customerRepository,
            ShopRepository shopRepository
    ) {
        this.categoryAnalyticsRepository = categoryAnalyticsRepository;
        this.customerRepository = customerRepository;
        this.shopRepository = shopRepository;
    }

    public CategorySpendingSummaryResponse getCategorySpending(

            Long customerId,
            Long shopId,
            Integer month,
            Integer year

    ) {

        // Validate month
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12"
            );
        }

        // Validate year
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

        // Calculate monthly date range
        LocalDate firstDay = LocalDate.of(year, month, 1);

        LocalDateTime startDate = firstDay.atStartOfDay();

        LocalDateTime endDate = firstDay
                .plusMonths(1)
                .atStartOfDay();

        // Retrieve category-wise spending
        List<CategorySpendingProjection> results =
                categoryAnalyticsRepository.findCategorySpending(
                        customerId,
                        shopId,
                        startDate,
                        endDate
                );

        // Calculate total spending
        BigDecimal totalSpent = results.stream()
                .map(CategorySpendingProjection::getTotalSpent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Convert query results into response DTOs
        List<CategorySpendingResponse> categories =
                results.stream()
                        .map(result -> {

                            BigDecimal percentage = BigDecimal.ZERO;

                            if (totalSpent.compareTo(BigDecimal.ZERO) > 0) {
                                percentage = result.getTotalSpent()
                                        .multiply(BigDecimal.valueOf(100))
                                        .divide(
                                                totalSpent,
                                                2,
                                                RoundingMode.HALF_UP
                                        );
                            }

                            return new CategorySpendingResponse(

                                    result.getCategoryId(),
                                    result.getCategoryName(),

                                    result.getTotalSpent(),

                                    result.getPurchasedItemCount(),
                                    result.getTotalPurchasedQuantity(),

                                    percentage

                            );
                        })
                        .toList();

        // Return category analytics summary
        return new CategorySpendingSummaryResponse(

                customer.getId(),
                customer.getFullName(),

                shop.getId(),
                shop.getShopName(),

                month,
                year,

                totalSpent,

                categories

        );
    }
}
