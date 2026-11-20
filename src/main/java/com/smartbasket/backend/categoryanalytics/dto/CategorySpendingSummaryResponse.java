
package com.smartbasket.backend.categoryanalytics.dto;

import java.math.BigDecimal;
import java.util.List;

public record CategorySpendingSummaryResponse(

        Long customerId,
        String customerName,

        Long shopId,
        String shopName,

        Integer month,
        Integer year,

        BigDecimal totalSpent,

        List<CategorySpendingResponse> categories

) {
}
