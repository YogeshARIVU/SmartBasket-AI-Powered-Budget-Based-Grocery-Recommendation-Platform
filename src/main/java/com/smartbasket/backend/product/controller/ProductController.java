
package com.smartbasket.backend.product.controller;

import com.smartbasket.backend.product.dto.ProductRequest;
import com.smartbasket.backend.product.dto.ProductResponse;
import com.smartbasket.backend.product.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE PRODUCT
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse response = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // SEARCH, FILTER, PAGINATION AND SORTING
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(

            @RequestParam(required = false) String keyword,

            @RequestParam(required = false) Long categoryId,

            @RequestParam(required = false) BigDecimal minPrice,

            @RequestParam(required = false) BigDecimal maxPrice,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "name") String sortBy,

            @RequestParam(defaultValue = "asc") String direction) {

        Page<ProductResponse> products =
                productService.getAllProducts(
                        keyword,
                        categoryId,
                        minPrice,
                        maxPrice,
                        page,
                        size,
                        sortBy,
                        direction
                );

        return ResponseEntity.ok(products);
    }

    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }

    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity.ok(
                productService.updateProduct(id, request)
        );
    }

    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}
