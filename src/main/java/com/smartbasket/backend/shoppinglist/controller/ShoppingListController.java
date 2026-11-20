package com.smartbasket.backend.shoppinglist.controller;

import com.smartbasket.backend.shoppinglist.dto.*;
import com.smartbasket.backend.shoppinglist.service.ShoppingListService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-lists")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(
            ShoppingListService shoppingListService
    ) {
        this.shoppingListService = shoppingListService;
    }

    @PostMapping
    public ResponseEntity<ShoppingListResponse> createShoppingList(
            @Valid @RequestBody ShoppingListRequest request
    ) {

        ShoppingListResponse response =
                shoppingListService.createShoppingList(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{listId}")
    public ResponseEntity<ShoppingListResponse> getShoppingList(
            @PathVariable Long listId
    ) {

        return ResponseEntity.ok(
                shoppingListService.getShoppingList(listId)
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ShoppingListResponse>> getCustomerShoppingLists(
            @PathVariable Long customerId
    ) {

        return ResponseEntity.ok(
                shoppingListService.getCustomerShoppingLists(customerId)
        );
    }

    @PostMapping("/{listId}/items")
    public ResponseEntity<ShoppingListResponse> addItem(
            @PathVariable Long listId,
            @Valid @RequestBody ShoppingListItemRequest request
    ) {

        return ResponseEntity.ok(
                shoppingListService.addItem(listId, request)
        );
    }

    @PutMapping("/{listId}/items/{itemId}")
    public ResponseEntity<ShoppingListResponse> updateItemQuantity(
            @PathVariable Long listId,
            @PathVariable Long itemId,
            @Valid @RequestBody ShoppingListItemUpdateRequest request
    ) {

        return ResponseEntity.ok(
                shoppingListService.updateItemQuantity(
                        listId,
                        itemId,
                        request
                )
        );
    }

    @DeleteMapping("/{listId}/items/{itemId}")
    public ResponseEntity<ShoppingListResponse> removeItem(
            @PathVariable Long listId,
            @PathVariable Long itemId
    ) {

        return ResponseEntity.ok(
                shoppingListService.removeItem(listId, itemId)
        );
    }

    @PatchMapping("/{listId}/items/{itemId}/purchase")
    public ResponseEntity<ShoppingListResponse> updatePurchaseStatus(
            @PathVariable Long listId,
            @PathVariable Long itemId,
            @Valid @RequestBody PurchaseStatusRequest request
    ) {

        return ResponseEntity.ok(
                shoppingListService.updatePurchaseStatus(
                        listId,
                        itemId,
                        request
                )
        );
    }
}