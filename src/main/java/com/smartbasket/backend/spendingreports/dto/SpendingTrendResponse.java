
package com.smartbasket.backend.spendingreports.dto;

import java.util.List;

public record SpendingTrendResponse(

        Long customerId,
        String customerName,

        Long shopId,
        String shopName,

        Integer numberOfMonths,

        List<MonthlySpendingReportResponse> monthlyReports

) {
}
