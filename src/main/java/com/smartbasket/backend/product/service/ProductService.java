
package com.smartbasket.backend.product.service;

import com.smartbasket.backend.category.entity.Category;
import com.smartbasket.backend.category.repository.CategoryRepository;
import com.smartbasket.backend.common.exception.ResourceNotFoundException;
import com.smartbasket.backend.product.dto.ProductRequest;
import com.smartbasket.backend.product.dto.ProductResponse;
import com.smartbasket.backend.product.entity.Product;
import com.smartbasket.backend.product.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse createProduct(ProductRequest request) {

        String sku = request.sku().trim();

        if (productRepository.existsBySkuIgnoreCase(sku)) {
            throw new IllegalArgumentException("Product SKU already exists");
        }

        Category category = findCategory(request.categoryId());

        Product product = new Product();

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setSku(sku);
        product.setPrice(request.price());
        product.setActive(true);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product = findProduct(id);

        return mapToResponse(product);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = findProduct(id);

        String sku = request.sku().trim();

        boolean duplicateSku = productRepository.existsBySkuIgnoreCase(sku);

        if (duplicateSku && !product.getSku().equalsIgnoreCase(sku)) {
            throw new IllegalArgumentException("Product SKU already exists");
        }

        Category category = findCategory(request.categoryId());

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setSku(sku);
        product.setPrice(request.price());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {

        Product product = findProduct(id);

        productRepository.delete(product);
    }

    private Product findProduct(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with ID: " + id));
    }

    private Category findCategory(Long categoryId) {

        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with ID: " + categoryId));
    }

    private ProductResponse mapToResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getPrice(),
                product.getActive(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
