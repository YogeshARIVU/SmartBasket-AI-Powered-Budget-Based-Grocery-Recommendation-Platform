
package com.smartbasket.backend.shoppinglist.service;

import com.smartbasket.backend.customer.entity.Customer;
import com.smartbasket.backend.customer.entity.CustomerBudget;
import com.smartbasket.backend.customer.repository.CustomerBudgetRepository;
import com.smartbasket.backend.customer.repository.CustomerRepository;

import com.smartbasket.backend.inventory.entity.Inventory;
import com.smartbasket.backend.inventory.entity.StockMovement;
import com.smartbasket.backend.inventory.entity.StockMovementType;
import com.smartbasket.backend.inventory.repository.InventoryRepository;
import com.smartbasket.backend.inventory.repository.StockMovementRepository;

import com.smartbasket.backend.product.entity.Product;
import com.smartbasket.backend.product.repository.ProductRepository;

import com.smartbasket.backend.shop.entity.Shop;
import com.smartbasket.backend.shop.repository.ShopRepository;

import com.smartbasket.backend.shoppinglist.dto.*;
import com.smartbasket.backend.shoppinglist.entity.ShoppingList;
import com.smartbasket.backend.shoppinglist.entity.ShoppingListItem;
import com.smartbasket.backend.shoppinglist.repository.ShoppingListRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final CustomerRepository customerRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerBudgetRepository customerBudgetRepository;
    private final StockMovementRepository stockMovementRepository;

    public ShoppingListService(
            ShoppingListRepository shoppingListRepository,
            CustomerRepository customerRepository,
            ShopRepository shopRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            CustomerBudgetRepository customerBudgetRepository,
            StockMovementRepository stockMovementRepository
    ) {
        this.shoppingListRepository = shoppingListRepository;
        this.customerRepository = customerRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.customerBudgetRepository = customerBudgetRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    // CREATE SHOPPING LIST

    @Transactional
    public ShoppingListResponse createShoppingList(
            ShoppingListRequest request
    ) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));

        Shop shop = shopRepository.findById(request.shopId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Shop not found"
                ));

        if (!Boolean.TRUE.equals(shop.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Shop is inactive"
            );
        }

        CustomerBudget budget = getCurrentBudget(
                customer.getId(),
                shop.getId()
        );

        if (budget.getRemainingBudget().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer has no remaining budget"
            );
        }

        ShoppingList shoppingList = new ShoppingList();

        shoppingList.setCustomer(customer);
        shoppingList.setShop(shop);
        shoppingList.setListName(request.listName().trim());

        return mapToResponse(
                shoppingListRepository.save(shoppingList),
                budget
        );
    }

    // GET SHOPPING LIST BY ID

    @Transactional(readOnly = true)
    public ShoppingListResponse getShoppingList(Long listId) {

        ShoppingList shoppingList = getListOrThrow(listId);

        CustomerBudget budget = getCurrentBudget(
                shoppingList.getCustomer().getId(),
                shoppingList.getShop().getId()
        );

        return mapToResponse(shoppingList, budget);
    }

    // GET CUSTOMER SHOPPING LISTS

    @Transactional(readOnly = true)
    public List<ShoppingListResponse> getCustomerShoppingLists(
            Long customerId
    ) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer not found"
            );
        }

        return shoppingListRepository
                .findAllByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(list -> mapToResponse(
                        list,
                        getCurrentBudget(
                                list.getCustomer().getId(),
                                list.getShop().getId()
                        )
                ))
                .toList();
    }

    // ADD PRODUCT TO SHOPPING LIST

    @Transactional
    public ShoppingListResponse addItem(
            Long listId,
            ShoppingListItemRequest request
    ) {

        ShoppingList shoppingList = getListOrThrow(listId);

        CustomerBudget budget = getCurrentBudget(
                shoppingList.getCustomer().getId(),
                shoppingList.getShop().getId()
        );

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product is inactive"
            );
        }

        Inventory inventory = getInventory(
                shoppingList.getShop().getId(),
                product.getId()
        );

        if (inventory.getAvailableQuantity() < request.quantity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient stock for this product"
            );
        }

        boolean alreadyExists = shoppingList.getItems()
                .stream()
                .anyMatch(item ->
                        item.getProduct().getId().equals(product.getId())
                );

        if (alreadyExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Product already exists in this shopping list"
            );
        }

        BigDecimal itemTotal = product.getPrice()
                .multiply(BigDecimal.valueOf(request.quantity()));

        validatePendingBudget(
                shoppingList,
                budget,
                itemTotal
        );

        ShoppingListItem item = new ShoppingListItem();

        item.setProduct(product);
        item.setQuantity(request.quantity());
        item.setUnitPrice(product.getPrice());
        item.setPurchased(false);
        item.setPurchasedAt(null);

        shoppingList.addItem(item);

        shoppingListRepository.save(shoppingList);

        return mapToResponse(shoppingList, budget);
    }

    // UPDATE PRODUCT QUANTITY

    @Transactional
    public ShoppingListResponse updateItemQuantity(
            Long listId,
            Long itemId,
            ShoppingListItemUpdateRequest request
    ) {

        ShoppingList shoppingList = getListOrThrow(listId);

        CustomerBudget budget = getCurrentBudget(
                shoppingList.getCustomer().getId(),
                shoppingList.getShop().getId()
        );

        ShoppingListItem item = getItemOrThrow(
                shoppingList,
                itemId
        );

        if (Boolean.TRUE.equals(item.getPurchased())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot update a purchased item. Undo purchase first."
            );
        }

        Inventory inventory = getInventory(
                shoppingList.getShop().getId(),
                item.getProduct().getId()
        );

        if (inventory.getAvailableQuantity() < request.quantity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient stock for requested quantity"
            );
        }

        BigDecimal oldTotal = item.getTotalPrice();

        BigDecimal newTotal = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.quantity()));

        BigDecimal additionalCost = newTotal.subtract(oldTotal);

        validatePendingBudget(
                shoppingList,
                budget,
                additionalCost
        );

        item.setQuantity(request.quantity());

        shoppingListRepository.save(shoppingList);

        return mapToResponse(shoppingList, budget);
    }

    // REMOVE PRODUCT FROM SHOPPING LIST

    @Transactional
    public ShoppingListResponse removeItem(
            Long listId,
            Long itemId
    ) {

        ShoppingList shoppingList = getListOrThrow(listId);

        CustomerBudget budget = getCurrentBudget(
                shoppingList.getCustomer().getId(),
                shoppingList.getShop().getId()
        );

        ShoppingListItem item = getItemOrThrow(
                shoppingList,
                itemId
        );

        if (Boolean.TRUE.equals(item.getPurchased())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Undo purchase before removing this item"
            );
        }

        shoppingList.removeItem(item);

        shoppingListRepository.save(shoppingList);

        return mapToResponse(shoppingList, budget);
    }

    // MARK OR UNMARK PRODUCT AS PURCHASED

    @Transactional
    public ShoppingListResponse updatePurchaseStatus(
            Long listId,
            Long itemId,
            PurchaseStatusRequest request
    ) {

        if (request == null || request.purchased() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Purchase status is required"
            );
        }

        ShoppingList shoppingList = getListOrThrow(listId);

        ShoppingListItem item = getItemOrThrow(
                shoppingList,
                itemId
        );

        CustomerBudget budget = getCurrentBudget(
                shoppingList.getCustomer().getId(),
                shoppingList.getShop().getId()
        );

        boolean currentlyPurchased =
                Boolean.TRUE.equals(item.getPurchased());

        boolean requestedPurchased = request.purchased();

        // Prevent duplicate stock deduction or restoration

        if (currentlyPurchased == requestedPurchased) {
            return mapToResponse(shoppingList, budget);
        }

        // Lock inventory row during purchase transaction

        Inventory inventory = inventoryRepository
                .findByShopIdAndProductIdForUpdate(
                        shoppingList.getShop().getId(),
                        item.getProduct().getId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product inventory not found for this shop"
                ));

        int previousQuantity = inventory.getAvailableQuantity();

        int quantity = item.getQuantity();

        BigDecimal itemTotal = item.getTotalPrice();

        BigDecimal spentAmount = budget.getSpentAmount();

        if (requestedPurchased) {

            // CHECK STOCK

            if (previousQuantity < quantity) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Insufficient stock available"
                );
            }

            // CHECK CUSTOMER BUDGET

            if (budget.getRemainingBudget().compareTo(itemTotal) < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Insufficient remaining budget to purchase this item"
                );
            }

            // DEDUCT INVENTORY

            int newQuantity = previousQuantity - quantity;

            inventory.setAvailableQuantity(newQuantity);

            inventoryRepository.save(inventory);

            // CREATE OUT STOCK MOVEMENT

            StockMovement movement = new StockMovement();

            movement.setInventory(inventory);
            movement.setMovementType(StockMovementType.OUT);
            movement.setQuantity(quantity);
            movement.setPreviousQuantity(previousQuantity);
            movement.setNewQuantity(newQuantity);
            movement.setReason("Shopping list purchase");

            stockMovementRepository.save(movement);

            // UPDATE CUSTOMER SPENDING

            budget.setSpentAmount(
                    spentAmount.add(itemTotal)
            );

            // MARK ITEM AS PURCHASED

            item.setPurchasedAt(LocalDateTime.now());
            item.setPurchased(true);

        } else {

            // RESTORE INVENTORY

            int newQuantity = previousQuantity + quantity;

            inventory.setAvailableQuantity(newQuantity);

            inventoryRepository.save(inventory);

            // CREATE IN STOCK MOVEMENT

            StockMovement movement = new StockMovement();

            movement.setInventory(inventory);
            movement.setMovementType(StockMovementType.IN);
            movement.setQuantity(quantity);
            movement.setPreviousQuantity(previousQuantity);
            movement.setNewQuantity(newQuantity);
            movement.setReason("Shopping list purchase reversal");

            stockMovementRepository.save(movement);

            // REVERSE CUSTOMER SPENDING

            BigDecimal updatedSpent = spentAmount.subtract(itemTotal);

            if (updatedSpent.compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid spending calculation"
                );
            }

            budget.setSpentAmount(updatedSpent);

            // MARK ITEM AS NOT PURCHASED

            item.setPurchasedAt(null);
            item.setPurchased(false);
        }

        customerBudgetRepository.save(budget);

        shoppingListRepository.save(shoppingList);

        return mapToResponse(shoppingList, budget);
    }

    // HELPER METHODS

    private ShoppingList getListOrThrow(Long listId) {

        return shoppingListRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Shopping list not found"
                ));
    }

    private ShoppingListItem getItemOrThrow(
            ShoppingList shoppingList,
            Long itemId
    ) {

        return shoppingList.getItems()
                .stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Shopping list item not found"
                ));
    }

    private CustomerBudget getCurrentBudget(
            Long customerId,
            Long shopId
    ) {

        LocalDate today = LocalDate.now();

        return customerBudgetRepository
                .findByCustomerIdAndShopIdAndMonthAndYear(
                        customerId,
                        shopId,
                        today.getMonthValue(),
                        today.getYear()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Monthly budget not found for this customer and shop"
                ));
    }

    private Inventory getInventory(
            Long shopId,
            Long productId
    ) {

        return inventoryRepository
                .findByShopIdAndProductId(shopId, productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product inventory not found for this shop"
                ));
    }

    private void validatePendingBudget(
            ShoppingList shoppingList,
            CustomerBudget budget,
            BigDecimal additionalCost
    ) {

        BigDecimal pendingCost = shoppingList.getItems()
                .stream()
                .filter(item -> !Boolean.TRUE.equals(item.getPurchased()))
                .map(ShoppingListItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal updatedPendingCost =
                pendingCost.add(additionalCost);

        if (updatedPendingCost.compareTo(
                budget.getRemainingBudget()
        ) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Shopping list exceeds the customer's remaining budget"
            );
        }
    }

    // RESPONSE MAPPER

    private ShoppingListResponse mapToResponse(
            ShoppingList shoppingList,
            CustomerBudget budget
    ) {

        List<ShoppingListItemResponse> itemResponses =
                shoppingList.getItems()
                        .stream()
                        .map(item -> new ShoppingListItemResponse(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getProduct().getSku(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getTotalPrice(),
                                item.getPurchased()
                        ))
                        .toList();

        BigDecimal totalEstimatedCost = shoppingList.getItems()
                .stream()
                .map(ShoppingListItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal purchasedCost = shoppingList.getItems()
                .stream()
                .filter(item -> Boolean.TRUE.equals(item.getPurchased()))
                .map(ShoppingListItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingCost = shoppingList.getItems()
                .stream()
                .filter(item -> !Boolean.TRUE.equals(item.getPurchased()))
                .map(ShoppingListItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ShoppingListResponse(
                shoppingList.getId(),
                shoppingList.getCustomer().getId(),
                shoppingList.getCustomer().getFullName(),
                shoppingList.getShop().getId(),
                shoppingList.getShop().getShopName(),
                shoppingList.getListName(),
                totalEstimatedCost,
                purchasedCost,
                pendingCost,
                budget.getRemainingBudget(),
                itemResponses,
                shoppingList.getCreatedAt(),
                shoppingList.getUpdatedAt()
        );
    }
}
