package com.smartbasket.backend.shoppinglist.repository;

import com.smartbasket.backend.shoppinglist.entity.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingListRepository
        extends JpaRepository<ShoppingList, Long> {

    List<ShoppingList> findAllByCustomerIdOrderByCreatedAtDesc(
            Long customerId
    );
}