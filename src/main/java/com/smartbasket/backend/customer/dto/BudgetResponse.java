
package com.smartbasket.backend.customer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BudgetResponse {

    private Long id;

    private Long customerId;
    private String customerName;

    private Long shopId;
    private String shopName;
    private String shopCode;

    private BigDecimal monthlyBudget;
    private BigDecimal spentAmount;
    private BigDecimal remainingBudget;

    private Integer month;
    private Integer year;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BudgetResponse() {
    }

    public BudgetResponse(
            Long id,
            Long customerId,
            String customerName,
            Long shopId,
            String shopName,
            String shopCode,
            BigDecimal monthlyBudget,
            BigDecimal spentAmount,
            BigDecimal remainingBudget,
            Integer month,
            Integer year,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopCode = shopCode;
        this.monthlyBudget = monthlyBudget;
        this.spentAmount = spentAmount;
        this.remainingBudget = remainingBudget;
        this.month = month;
        this.year = year;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getShopId() {
        return shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopCode() {
        return shopCode;
    }

    public BigDecimal getMonthlyBudget() {
        return monthlyBudget;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public BigDecimal getRemainingBudget() {
        return remainingBudget;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getYear() {
        return year;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
