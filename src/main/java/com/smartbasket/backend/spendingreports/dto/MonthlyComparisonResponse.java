
package com.smartbasket.backend.spendingreports.dto;

import java.math.BigDecimal;

public record MonthlyComparisonResponse(

        MonthlySpendingReportResponse currentMonth,
        MonthlySpendingReportResponse previousMonth,

        BigDecimal spendingDifference,
        BigDecimal percentageChange,

        String spendingTrend

) {
}
