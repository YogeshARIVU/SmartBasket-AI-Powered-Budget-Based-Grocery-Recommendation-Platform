
package com.smartbasket.backend.inventory.service;

import com.smartbasket.backend.inventory.dto.InventoryRequest;
import com.smartbasket.backend.inventory.dto.InventoryResponse;
import com.smartbasket.backend.inventory.dto.StockUpdateRequest;
import com.smartbasket.backend.inventory.entity.Inventory;
import com.smartbasket.backend.inventory.enums.StockMovementType;
import com.smartbasket.backend.inventory.repository.InventoryRepository;
import com.smartbasket.backend.product.entity.Product;
import com.smartbasket.backend.product.repository.ProductRepository;
import com.smartbasket.backend.shop.entity.Shop;
import com.smartbasket.backend.shop.repository.ShopRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository,
            ShopRepository shopRepository
    ) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
    }

    // Create inventory for a particular shop and product
    public InventoryResponse createInventory(InventoryRequest request) {

        Shop shop = findShop(request.shopId());

        if (!Boolean.TRUE.equals(shop.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Shop is inactive"
            );
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with ID: " + request.productId()
                ));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product is inactive"
            );
        }

        if (inventoryRepository.existsByShopIdAndProductId(
                request.shopId(),
                request.productId()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Inventory already exists for this product in this shop"
            );
        }

        Inventory inventory = new Inventory();
        inventory.setShop(shop);
        inventory.setProduct(product);
        inventory.setAvailableQuantity(request.availableQuantity());
        inventory.setMinimumStockLevel(request.minimumStockLevel());

        return mapToResponse(inventoryRepository.save(inventory));
    }

    // Get inventory across all shops
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {

        return inventoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get inventory belonging to one shop
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryByShopId(Long shopId) {

        findShop(shopId);

        return inventoryRepository.findAllByShopId(shopId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get inventory by inventory ID
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryById(Long id) {

        return mapToResponse(findInventory(id));
    }

    // Get a product's inventory in a particular shop
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByShopAndProduct(
            Long shopId,
            Long productId
    ) {

        findShop(shopId);

        Inventory inventory = inventoryRepository
                .findByShopIdAndProductId(shopId, productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Inventory not found for shop ID: " + shopId
                                + " and product ID: " + productId
                ));

        return mapToResponse(inventory);
    }

    // Increase or decrease stock
    public InventoryResponse updateStock(
            Long inventoryId,
            StockUpdateRequest request
    ) {

        Inventory inventory = findInventory(inventoryId);

        int currentQuantity = inventory.getAvailableQuantity();
        int requestedQuantity = request.quantity();

        if (request.movementType() == StockMovementType.INCREASE) {

            try {
                inventory.setAvailableQuantity(
                        Math.addExact(currentQuantity, requestedQuantity)
                );
            } catch (ArithmeticException exception) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Stock quantity exceeds the supported limit"
                );
            }

        } else if (request.movementType() == StockMovementType.DECREASE) {

            if (currentQuantity < requestedQuantity) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Insufficient stock. Available quantity: " + currentQuantity
                );
            }

            inventory.setAvailableQuantity(currentQuantity - requestedQuantity);

        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid stock movement type"
            );
        }

        return mapToResponse(inventoryRepository.save(inventory));
    }

    // Update minimum stock level
    public InventoryResponse updateMinimumStockLevel(
            Long inventoryId,
            Integer minimumStockLevel
    ) {

        if (minimumStockLevel == null || minimumStockLevel < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Minimum stock level cannot be negative"
            );
        }

        Inventory inventory = findInventory(inventoryId);
        inventory.setMinimumStockLevel(minimumStockLevel);

        return mapToResponse(inventoryRepository.save(inventory));
    }

    // Get low-stock inventory across all shops
    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStockItems() {

        return inventoryRepository.findLowStockItems()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get low-stock inventory for one shop
    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStockItemsByShopId(Long shopId) {

        findShop(shopId);

        return inventoryRepository.findLowStockItemsByShopId(shopId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Validate whether a shop has enough stock for a product
    @Transactional(readOnly = true)
    public void validateStock(
            Long shopId,
            Long productId,
            Integer requestedQuantity
    ) {

        if (requestedQuantity == null || requestedQuantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Requested quantity must be greater than zero"
            );
        }

        Inventory inventory = inventoryRepository
                .findByShopIdAndProductId(shopId, productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Inventory not found for this shop and product"
                ));

        if (inventory.getAvailableQuantity() < requestedQuantity) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient stock. Available quantity: "
                            + inventory.getAvailableQuantity()
            );
        }
    }

    // Find inventory by ID
    private Inventory findInventory(Long id) {

        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Inventory not found with ID: " + id
                ));
    }

    // Find shop by ID
    private Shop findShop(Long shopId) {

        return shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Shop not found with ID: " + shopId
                ));
    }

    // Convert entity into response DTO
    private InventoryResponse mapToResponse(Inventory inventory) {

        Product product = inventory.getProduct();
        Shop shop = inventory.getShop();

        return new InventoryResponse(
                inventory.getId(),
                shop.getId(),
                shop.getShopName(),
                shop.getShopCode(),
                product.getId(),
                product.getName(),
                product.getSku(),
                inventory.getAvailableQuantity(),
                inventory.getMinimumStockLevel(),
                inventory.getAvailableQuantity()
                        <= inventory.getMinimumStockLevel(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }
}
