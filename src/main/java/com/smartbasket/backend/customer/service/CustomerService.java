
package com.smartbasket.backend.customer.service;

import com.smartbasket.backend.common.exception.DuplicateResourceException;
import com.smartbasket.backend.common.exception.ResourceNotFoundException;

import com.smartbasket.backend.customer.dto.CustomerRequest;
import com.smartbasket.backend.customer.dto.CustomerResponse;
import com.smartbasket.backend.customer.entity.Customer;
import com.smartbasket.backend.customer.repository.CustomerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Create Customer
    public CustomerResponse createCustomer(CustomerRequest request) {

        String email = request.getEmail().trim();
        String phoneNumber = request.getPhoneNumber().trim();

        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException(
                    "Customer email already exists: " + email
            );
        }

        if (customerRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicateResourceException(
                    "Customer phone number already exists: " + phoneNumber
            );
        }

        Customer customer = new Customer();

        mapRequestToEntity(request, customer);

        return mapToResponse(customerRepository.save(customer));
    }

    // Get All Customers
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get Customer By ID
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {

        Customer customer = findCustomerById(id);

        return mapToResponse(customer);
    }

    // Update Customer
    public CustomerResponse updateCustomer(
            Long id,
            CustomerRequest request) {

        Customer customer = findCustomerById(id);

        String email = request.getEmail().trim();
        String phoneNumber = request.getPhoneNumber().trim();

        if (customerRepository.existsByEmailIgnoreCaseAndIdNot(
                email, id)) {

            throw new DuplicateResourceException(
                    "Customer email already exists: " + email
            );
        }

        if (customerRepository.existsByPhoneNumberAndIdNot(
                phoneNumber, id)) {

            throw new DuplicateResourceException(
                    "Customer phone number already exists: " + phoneNumber
            );
        }

        mapRequestToEntity(request, customer);

        return mapToResponse(customerRepository.save(customer));
    }

    // Delete Customer
    public void deleteCustomer(Long id) {

        Customer customer = findCustomerById(id);

        customerRepository.delete(customer);
    }

    // Search Customers
    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomers(String name) {

        if (name == null || name.isBlank()) {
            return getAllCustomers();
        }

        return customerRepository
                .findByFullNameContainingIgnoreCase(name.trim())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Find Customer
    private Customer findCustomerById(Long id) {

        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with ID: " + id
                        )
                );
    }

    // Map Request To Entity
    private void mapRequestToEntity(
            CustomerRequest request,
            Customer customer) {

        customer.setFullName(request.getFullName().trim());
        customer.setEmail(request.getEmail().trim());
        customer.setPhoneNumber(request.getPhoneNumber().trim());
    }

    // Map Entity To Response
    private CustomerResponse mapToResponse(Customer customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
