
package com.smartbasket.backend.shop.repository;

import com.smartbasket.backend.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    boolean existsByShopCodeIgnoreCase(String shopCode);

    boolean existsByShopCodeIgnoreCaseAndIdNot(
            String shopCode,
            Long id
    );

    List<Shop> findByShopNameContainingIgnoreCase(String shopName);
}
