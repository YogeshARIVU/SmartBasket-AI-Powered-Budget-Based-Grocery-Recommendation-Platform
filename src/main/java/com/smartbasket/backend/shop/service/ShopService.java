
package com.smartbasket.backend.shop.service;

import com.smartbasket.backend.common.exception.DuplicateResourceException;
import com.smartbasket.backend.common.exception.ResourceNotFoundException;
import com.smartbasket.backend.shop.dto.ShopRequest;
import com.smartbasket.backend.shop.dto.ShopResponse;
import com.smartbasket.backend.shop.entity.Shop;
import com.smartbasket.backend.shop.repository.ShopRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ShopService {

    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    // Create Shop
    public ShopResponse createShop(ShopRequest request) {

        String shopCode = request.getShopCode().trim();

        if (shopRepository.existsByShopCodeIgnoreCase(shopCode)) {
            throw new DuplicateResourceException(
                    "Shop code already exists: " + shopCode
            );
        }

        Shop shop = new Shop();
        mapRequestToEntity(request, shop);

        try {
            return mapToResponse(shopRepository.saveAndFlush(shop));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateResourceException(
                    "Shop code already exists: " + shopCode
            );
        }
    }

    // Get All Shops
    @Transactional(readOnly = true)
    public List<ShopResponse> getAllShops() {

        return shopRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get Shop By ID
    @Transactional(readOnly = true)
    public ShopResponse getShopById(Long id) {

        Shop shop = findShopById(id);

        return mapToResponse(shop);
    }

    // Update Shop
    public ShopResponse updateShop(Long id, ShopRequest request) {

        Shop shop = findShopById(id);

        String shopCode = request.getShopCode().trim();

        if (shopRepository.existsByShopCodeIgnoreCaseAndIdNot(
                shopCode, id)) {

            throw new DuplicateResourceException(
                    "Shop code already exists: " + shopCode
            );
        }

        mapRequestToEntity(request, shop);

        try {
            return mapToResponse(shopRepository.saveAndFlush(shop));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateResourceException(
                    "Shop code already exists: " + shopCode
            );
        }
    }

    // Delete Shop
    public void deleteShop(Long id) {

        Shop shop = findShopById(id);

        shopRepository.delete(shop);
    }

    // Search Shops By Name
    @Transactional(readOnly = true)
    public List<ShopResponse> searchShops(String name) {

        if (name == null || name.isBlank()) {
            return getAllShops();
        }

        return shopRepository
                .findByShopNameContainingIgnoreCase(name.trim())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Find Shop
    private Shop findShopById(Long id) {

        return shopRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shop not found with ID: " + id
                        )
                );
    }

    // Map Request To Entity
    private void mapRequestToEntity(
            ShopRequest request,
            Shop shop) {

        shop.setShopName(request.getShopName().trim());
        shop.setShopCode(request.getShopCode().trim());
        shop.setOwnerName(request.getOwnerName().trim());
        shop.setEmail(
                request.getEmail() == null || request.getEmail().isBlank()
                        ? null : request.getEmail().trim()
        );
        shop.setPhoneNumber(request.getPhoneNumber().trim());
        shop.setAddress(request.getAddress().trim());
        shop.setCity(request.getCity().trim());
        shop.setState(request.getState().trim());
        shop.setPincode(request.getPincode().trim());

        shop.setActive(
                request.getActive() == null
                        ? true
                        : request.getActive()
        );
    }

    // Map Entity To Response
    private ShopResponse mapToResponse(Shop shop) {

        return new ShopResponse(
                shop.getId(),
                shop.getShopName(),
                shop.getShopCode(),
                shop.getOwnerName(),
                shop.getEmail(),
                shop.getPhoneNumber(),
                shop.getAddress(),
                shop.getCity(),
                shop.getState(),
                shop.getPincode(),
                shop.getActive(),
                shop.getCreatedAt(),
                shop.getUpdatedAt()
        );
    }
}
