
package com.smartbasket.backend.customer.repository;

import com.smartbasket.backend.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    List<Customer> findByFullNameContainingIgnoreCase(String fullName);
}
