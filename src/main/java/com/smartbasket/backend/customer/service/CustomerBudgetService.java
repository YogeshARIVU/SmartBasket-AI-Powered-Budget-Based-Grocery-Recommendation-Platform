
package com.smartbasket.backend.customer.service;

import com.smartbasket.backend.common.exception.DuplicateResourceException;
import com.smartbasket.backend.common.exception.ResourceNotFoundException;

import com.smartbasket.backend.customer.dto.BudgetRequest;
import com.smartbasket.backend.customer.dto.BudgetResponse;
import com.smartbasket.backend.customer.entity.Customer;
import com.smartbasket.backend.customer.entity.CustomerBudget;
import com.smartbasket.backend.customer.repository.CustomerBudgetRepository;
import com.smartbasket.backend.customer.repository.CustomerRepository;

import com.smartbasket.backend.shop.entity.Shop;
import com.smartbasket.backend.shop.repository.ShopRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CustomerBudgetService {

    private final CustomerBudgetRepository budgetRepository;
    private final CustomerRepository customerRepository;
    private final ShopRepository shopRepository;

    public CustomerBudgetService(
            CustomerBudgetRepository budgetRepository,
            CustomerRepository customerRepository,
            ShopRepository shopRepository) {

        this.budgetRepository = budgetRepository;
        this.customerRepository = customerRepository;
        this.shopRepository = shopRepository;
    }

    // Create Budget
    public BudgetResponse createBudget(BudgetRequest request) {

        Customer customer = findCustomer(request.getCustomerId());

        Shop shop = findShop(request.getShopId());

        boolean exists =
                budgetRepository.existsByCustomerIdAndShopIdAndMonthAndYear(
                        request.getCustomerId(),
                        request.getShopId(),
                        request.getMonth(),
                        request.getYear()
                );

        if (exists) {
            throw new DuplicateResourceException(
                    "Budget already exists for this customer, shop and month"
            );
        }

        CustomerBudget budget = new CustomerBudget();

        budget.setCustomer(customer);
        budget.setShop(shop);
        budget.setMonthlyBudget(request.getMonthlyBudget());
        budget.setSpentAmount(BigDecimal.ZERO);
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        return mapToResponse(budgetRepository.save(budget));
    }

    // Get All Budgets
    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgets() {

        return budgetRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get Budget By ID
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(Long id) {

        CustomerBudget budget = findBudget(id);

        return mapToResponse(budget);
    }

    // Get Budgets By Customer
    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByCustomer(Long customerId) {

        findCustomer(customerId);

        return budgetRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get Budgets By Shop
    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByShop(Long shopId) {

        findShop(shopId);

        return budgetRepository.findByShopId(shopId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get Customer Budget By Month And Year
    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByCustomerAndPeriod(
            Long customerId,
            Integer month,
            Integer year) {

        findCustomer(customerId);

        return budgetRepository
                .findByCustomerIdAndMonthAndYear(
                        customerId,
                        month,
                        year
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Update Budget
    public BudgetResponse updateBudget(
            Long id,
            BudgetRequest request) {

        CustomerBudget budget = findBudget(id);

        Customer customer = findCustomer(request.getCustomerId());

        Shop shop = findShop(request.getShopId());

        boolean exists =
                budgetRepository
                        .existsByCustomerIdAndShopIdAndMonthAndYearAndIdNot(
                                request.getCustomerId(),
                                request.getShopId(),
                                request.getMonth(),
                                request.getYear(),
                                id
                        );

        if (exists) {
            throw new DuplicateResourceException(
                    "Budget already exists for this customer, shop and month"
            );
        }

        budget.setCustomer(customer);
        budget.setShop(shop);
        budget.setMonthlyBudget(request.getMonthlyBudget());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        // Preserve existing spentAmount.

        return mapToResponse(budgetRepository.save(budget));
    }

    // Delete Budget
    public void deleteBudget(Long id) {

        CustomerBudget budget = findBudget(id);

        budgetRepository.delete(budget);
    }

    // Find Customer
    private Customer findCustomer(Long customerId) {

        return customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with ID: " + customerId
                        )
                );
    }

    // Find Shop
    private Shop findShop(Long shopId) {

        return shopRepository.findById(shopId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shop not found with ID: " + shopId
                        )
                );
    }

    // Find Budget
    private CustomerBudget findBudget(Long id) {

        return budgetRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Budget not found with ID: " + id
                        )
                );
    }

    // Map Entity To Response
    private BudgetResponse mapToResponse(CustomerBudget budget) {

        return new BudgetResponse(
                budget.getId(),
                budget.getCustomer().getId(),
                budget.getCustomer().getFullName(),
                budget.getShop().getId(),
                budget.getShop().getShopName(),
                budget.getShop().getShopCode(),
                budget.getMonthlyBudget(),
                budget.getSpentAmount(),
                budget.getRemainingBudget(),
                budget.getMonth(),
                budget.getYear(),
                budget.getCreatedAt(),
                budget.getUpdatedAt()
        );
    }
}
