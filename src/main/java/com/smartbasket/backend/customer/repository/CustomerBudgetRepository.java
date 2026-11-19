
package com.smartbasket.backend.customer.repository;

import com.smartbasket.backend.customer.entity.CustomerBudget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerBudgetRepository extends JpaRepository<CustomerBudget, Long> {

    boolean existsByCustomerIdAndShopIdAndMonthAndYear(
            Long customerId,
            Long shopId,
            Integer month,
            Integer year
    );

    boolean existsByCustomerIdAndShopIdAndMonthAndYearAndIdNot(
            Long customerId,
            Long shopId,
            Integer month,
            Integer year,
            Long id
    );

    List<CustomerBudget> findByCustomerId(Long customerId);

    List<CustomerBudget> findByShopId(Long shopId);

    List<CustomerBudget> findByCustomerIdAndMonthAndYear(
            Long customerId,
            Integer month,
            Integer year
    );

    Optional<CustomerBudget> findByCustomerIdAndShopIdAndMonthAndYear(
            Long customerId,
            Long shopId,
            Integer month,
            Integer year
    );
}
