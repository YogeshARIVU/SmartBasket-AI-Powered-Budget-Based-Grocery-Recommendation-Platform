
package com.smartbasket.backend.spendingreports.service;

import com.smartbasket.backend.customer.entity.Customer;
import com.smartbasket.backend.customer.entity.CustomerBudget;
import com.smartbasket.backend.customer.repository.CustomerBudgetRepository;
import com.smartbasket.backend.customer.repository.CustomerRepository;
import com.smartbasket.backend.shop.entity.Shop;
import com.smartbasket.backend.shop.repository.ShopRepository;
import com.smartbasket.backend.shoppinglist.entity.ShoppingListItem;
import com.smartbasket.backend.spendingreports.dto.MonthlyComparisonResponse;
import com.smartbasket.backend.spendingreports.dto.MonthlySpendingReportResponse;
import com.smartbasket.backend.spendingreports.dto.SpendingTrendResponse;
import com.smartbasket.backend.spendingreports.repository.SpendingReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SpendingReportService {

    private final SpendingReportRepository spendingReportRepository;
    private final CustomerRepository customerRepository;
    private final ShopRepository shopRepository;
    private final CustomerBudgetRepository customerBudgetRepository;

    public SpendingReportService(
            SpendingReportRepository spendingReportRepository,
            CustomerRepository customerRepository,
            ShopRepository shopRepository,
            CustomerBudgetRepository customerBudgetRepository
    ) {
        this.spendingReportRepository = spendingReportRepository;
        this.customerRepository = customerRepository;
        this.shopRepository = shopRepository;
        this.customerBudgetRepository = customerBudgetRepository;
    }

    // 1. Get monthly spending report
    public MonthlySpendingReportResponse getMonthlyReport(
            Long customerId,
            Long shopId,
            Integer month,
            Integer year
    ) {
        validateMonthAndYear(month, year);

        Customer customer = getCustomer(customerId);
        Shop shop = getShop(shopId);

        return buildMonthlyReport(
                customer,
                shop,
                YearMonth.of(year, month)
        );
    }

    // 2. Compare selected month with previous month
    public MonthlyComparisonResponse compareMonthlySpending(
            Long customerId,
            Long shopId,
            Integer month,
            Integer year
    ) {
        validateMonthAndYear(month, year);

        Customer customer = getCustomer(customerId);
        Shop shop = getShop(shopId);

        YearMonth currentPeriod = YearMonth.of(year, month);
        YearMonth previousPeriod = currentPeriod.minusMonths(1);

        MonthlySpendingReportResponse currentReport =
                buildMonthlyReport(customer, shop, currentPeriod);

        MonthlySpendingReportResponse previousReport =
                buildMonthlyReport(customer, shop, previousPeriod);

        BigDecimal difference = currentReport.totalSpent()
                .subtract(previousReport.totalSpent());

        BigDecimal percentageChange = null;

        if (previousReport.totalSpent().compareTo(BigDecimal.ZERO) > 0) {
            percentageChange = difference
                    .multiply(BigDecimal.valueOf(100))
                    .divide(
                            previousReport.totalSpent(),
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        String trend;

        if (difference.compareTo(BigDecimal.ZERO) > 0) {
            trend = "INCREASED";
        } else if (difference.compareTo(BigDecimal.ZERO) < 0) {
            trend = "DECREASED";
        } else {
            trend = "UNCHANGED";
        }

        return new MonthlyComparisonResponse(
                currentReport,
                previousReport,
                difference,
                percentageChange,
                trend
        );
    }

    // 3. Get spending trend for multiple months
    public SpendingTrendResponse getSpendingTrend(
            Long customerId,
            Long shopId,
            Integer numberOfMonths
    ) {
        if (numberOfMonths == null
                || numberOfMonths < 1
                || numberOfMonths > 24) {
            throw new IllegalArgumentException(
                    "Number of months must be between 1 and 24"
            );
        }

        Customer customer = getCustomer(customerId);
        Shop shop = getShop(shopId);

        YearMonth currentPeriod = YearMonth.now();

        List<MonthlySpendingReportResponse> reports =
                new ArrayList<>();

        // Oldest month first, latest month last
        for (int i = numberOfMonths - 1; i >= 0; i--) {

            YearMonth period = currentPeriod.minusMonths(i);

            reports.add(
                    buildMonthlyReport(customer, shop, period)
            );
        }

        return new SpendingTrendResponse(
                customer.getId(),
                customer.getFullName(),
                shop.getId(),
                shop.getShopName(),
                numberOfMonths,
                reports
        );
    }

    // Build report for one month
    private MonthlySpendingReportResponse buildMonthlyReport(
            Customer customer,
            Shop shop,
            YearMonth period
    ) {

        LocalDate firstDay = period.atDay(1);

        LocalDateTime startDate = firstDay.atStartOfDay();

        LocalDateTime endDate = firstDay
                .plusMonths(1)
                .atStartOfDay();

        List<ShoppingListItem> items =
                spendingReportRepository.findPurchasedItemsForPeriod(
                        customer.getId(),
                        shop.getId(),
                        startDate,
                        endDate
                );

        BigDecimal totalSpent = BigDecimal.ZERO;
        long totalPurchasedQuantity = 0;

        for (ShoppingListItem item : items) {
            totalSpent = totalSpent.add(item.getTotalPrice());
            totalPurchasedQuantity += item.getQuantity();
        }

        CustomerBudget budget = customerBudgetRepository
                .findByCustomerIdAndShopIdAndMonthAndYear(
                        customer.getId(),
                        shop.getId(),
                        period.getMonthValue(),
                        period.getYear()
                )
                .orElse(null);

        BigDecimal monthlyBudget = budget == null
                ? BigDecimal.ZERO
                : budget.getMonthlyBudget();

        BigDecimal remainingBudget =
                monthlyBudget.subtract(totalSpent);

        BigDecimal utilization = BigDecimal.ZERO;

        if (monthlyBudget.compareTo(BigDecimal.ZERO) > 0) {
            utilization = totalSpent
                    .multiply(BigDecimal.valueOf(100))
                    .divide(
                            monthlyBudget,
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        return new MonthlySpendingReportResponse(
                customer.getId(),
                customer.getFullName(),
                shop.getId(),
                shop.getShopName(),
                period.getMonthValue(),
                period.getYear(),
                monthlyBudget,
                totalSpent,
                remainingBudget,
                utilization,
                (long) items.size(),
                totalPurchasedQuantity
        );
    }

    private Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found with ID: " + customerId
                ));
    }

    private Shop getShop(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Shop not found with ID: " + shopId
                ));
    }

    private void validateMonthAndYear(Integer month, Integer year) {
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
    }
}
