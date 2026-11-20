
package com.smartbasket.backend.shoppinglist.service;

import com.smartbasket.backend.shoppinglist.dto.PurchaseHistoryResponse;
import com.smartbasket.backend.shoppinglist.entity.ShoppingListItem;
import com.smartbasket.backend.shoppinglist.repository.PurchaseHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;

    public PurchaseHistoryService(
            PurchaseHistoryRepository purchaseHistoryRepository
    ) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }

    // 1. Get all purchase history of a customer
    public List<PurchaseHistoryResponse> getCustomerPurchaseHistory(
            Long customerId
    ) {
        List<ShoppingListItem> items =
                purchaseHistoryRepository
                        .findAllByShoppingListCustomerIdAndPurchasedTrueOrderByPurchasedAtDesc(
                                customerId
                        );

        return mapToResponses(items);
    }

    // 2. Get all purchase history of a shop
    public List<PurchaseHistoryResponse> getShopPurchaseHistory(
            Long shopId
    ) {
        List<ShoppingListItem> items =
                purchaseHistoryRepository
                        .findAllByShoppingListShopIdAndPurchasedTrueOrderByPurchasedAtDesc(
                                shopId
                        );

        return mapToResponses(items);
    }

    // 3. Get customer purchase history within a date range
    public List<PurchaseHistoryResponse> getCustomerPurchaseHistoryByDateRange(
            Long customerId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }

        List<ShoppingListItem> items =
                purchaseHistoryRepository
                        .findAllByShoppingListCustomerIdAndPurchasedTrueAndPurchasedAtBetweenOrderByPurchasedAtDesc(
                                customerId,
                                startDate.atStartOfDay(),
                                endDate.plusDays(1).atStartOfDay().minusNanos(1)
                        );

        return mapToResponses(items);
    }

    // 4. Get customer purchase history from a particular shop
    public List<PurchaseHistoryResponse> getCustomerPurchaseHistoryByShop(
            Long customerId,
            Long shopId
    ) {
        List<ShoppingListItem> items =
                purchaseHistoryRepository
                        .findAllByShoppingListCustomerIdAndShoppingListShopIdAndPurchasedTrueOrderByPurchasedAtDesc(
                                customerId,
                                shopId
                        );

        return mapToResponses(items);
    }

    // Convert entity objects into response DTOs
    private List<PurchaseHistoryResponse> mapToResponses(
            List<ShoppingListItem> items
    ) {
        return items.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PurchaseHistoryResponse mapToResponse(
            ShoppingListItem item
    ) {
        return new PurchaseHistoryResponse(
                item.getId(),

                item.getShoppingList().getId(),
                item.getShoppingList().getListName(),

                item.getShoppingList().getCustomer().getId(),
                item.getShoppingList().getCustomer().getFullName(),

                item.getShoppingList().getShop().getId(),
                item.getShoppingList().getShop().getShopName(),

                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getSku(),

                item.getQuantity(),

                item.getUnitPrice(),
                item.getTotalPrice(),

                item.getPurchasedAt()
        );
    }
}
