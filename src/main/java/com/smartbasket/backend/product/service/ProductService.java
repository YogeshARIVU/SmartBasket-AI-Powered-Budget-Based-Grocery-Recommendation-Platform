
package com.smartbasket.backend.product.service;

import com.smartbasket.backend.category.entity.Category;
import com.smartbasket.backend.category.repository.CategoryRepository;
import com.smartbasket.backend.common.exception.DuplicateResourceException;
import com.smartbasket.backend.common.exception.ResourceNotFoundException;
import com.smartbasket.backend.product.dto.ProductRequest;
import com.smartbasket.backend.product.dto.ProductResponse;
import com.smartbasket.backend.product.entity.Product;
import com.smartbasket.backend.product.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("name", "price", "createdAt", "sku");

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // CREATE PRODUCT
    public ProductResponse createProduct(ProductRequest request) {

        String sku = request.sku().trim();

        if (productRepository.existsBySkuIgnoreCase(sku)) {
            throw new DuplicateResourceException(
                    "Product with SKU " + sku + " already exists"
            );
        }

        Category category = findCategory(request.categoryId());

        Product product = new Product();

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setSku(sku);
        product.setPrice(request.price());
        product.setUnitQuantity(request.unitQuantity());
        product.setUnit(request.unit());
        product.setActive(true);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    // SEARCH, FILTER, PAGINATION AND SORTING
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(
            String keyword,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String direction) {

        // PAGE VALIDATION
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative"
            );
        }

        // SIZE VALIDATION
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        // PRICE VALIDATION
        if (minPrice != null &&
                minPrice.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be negative"
            );
        }

        if (maxPrice != null &&
                maxPrice.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Maximum price cannot be negative"
            );
        }

        if (minPrice != null && maxPrice != null &&
                minPrice.compareTo(maxPrice) > 0) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be greater than maximum price"
            );
        }

        // SORT FIELD VALIDATION
        sortBy = (sortBy == null || sortBy.isBlank())
                ? "name"
                : sortBy.trim();

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sort field. Allowed fields: "
                            + ALLOWED_SORT_FIELDS
            );
        }

        // SORT DIRECTION VALIDATION
        Sort.Direction sortDirection;

        try {
            sortDirection = Sort.Direction.fromString(
                    direction == null || direction.isBlank()
                            ? "asc"
                            : direction.trim()
            );
        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "Direction must be either ASC or DESC"
            );
        }

        // PAGINATION
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        // DYNAMIC SEARCH SPECIFICATION
        Specification<Product> specification =
                (root, query, criteriaBuilder) -> {

                    List<Predicate> predicates = new ArrayList<>();

                    // SEARCH BY NAME, DESCRIPTION OR SKU
                    if (keyword != null && !keyword.isBlank()) {

                        String searchKeyword =
                                "%" + escapeLikePattern(
                                        keyword.trim().toLowerCase()
                                ) + "%";

                        Predicate namePredicate =
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get("name")
                                        ),
                                        searchKeyword,
                                        '\\'
                                );

                        Predicate descriptionPredicate =
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get("description")
                                        ),
                                        searchKeyword,
                                        '\\'
                                );

                        Predicate skuPredicate =
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get("sku")
                                        ),
                                        searchKeyword,
                                        '\\'
                                );

                        predicates.add(
                                criteriaBuilder.or(
                                        namePredicate,
                                        descriptionPredicate,
                                        skuPredicate
                                )
                        );
                    }

                    // FILTER BY CATEGORY
                    if (categoryId != null) {

                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get("category").get("id"),
                                        categoryId
                                )
                        );
                    }

                    // MINIMUM PRICE
                    if (minPrice != null) {

                        predicates.add(
                                criteriaBuilder.greaterThanOrEqualTo(
                                        root.get("price"),
                                        minPrice
                                )
                        );
                    }

                    // MAXIMUM PRICE
                    if (maxPrice != null) {

                        predicates.add(
                                criteriaBuilder.lessThanOrEqualTo(
                                        root.get("price"),
                                        maxPrice
                                )
                        );
                    }

                    return criteriaBuilder.and(
                            predicates.toArray(new Predicate[0])
                    );
                };

        return productRepository.findAll(specification, pageable)
                .map(this::mapToResponse);
    }

    // ESCAPE SQL LIKE WILDCARDS
    private String escapeLikePattern(String value) {

        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    // GET PRODUCT BY ID
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product = findProduct(id);

        return mapToResponse(product);
    }

    // UPDATE PRODUCT
    public ProductResponse updateProduct(
            Long id,
            ProductRequest request) {

        Product product = findProduct(id);

        String sku = request.sku().trim();

        if (productRepository.existsBySkuIgnoreCaseAndIdNot(sku, id)) {
            throw new DuplicateResourceException(
                    "Another product already uses SKU " + sku
            );
        }

        Category category = findCategory(request.categoryId());

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setSku(sku);
        product.setPrice(request.price());
        product.setUnitQuantity(request.unitQuantity());
        product.setUnit(request.unit());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    // DELETE PRODUCT
    public void deleteProduct(Long id) {

        Product product = findProduct(id);

        productRepository.delete(product);
    }

    // FIND PRODUCT
    private Product findProduct(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with ID: " + id
                        )
                );
    }

    // FIND CATEGORY
    private Category findCategory(Long categoryId) {

        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with ID: " + categoryId
                        )
                );
    }

    private ProductResponse mapToResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getPrice(),
                product.getUnitQuantity(),
                product.getUnit(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
