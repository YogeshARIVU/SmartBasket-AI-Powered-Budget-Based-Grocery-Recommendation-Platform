
package com.smartbasket.backend.category.service;

import com.smartbasket.backend.category.dto.CategoryRequest;
import com.smartbasket.backend.category.dto.CategoryResponse;
import com.smartbasket.backend.category.entity.Category;
import com.smartbasket.backend.category.repository.CategoryRepository;
import com.smartbasket.backend.common.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // CREATE CATEGORY
    public CategoryResponse createCategory(CategoryRequest request) {

        String name = request.name().trim();

        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException(
                    "Category already exists"
            );
        }

        Category category = new Category();

        category.setName(name);
        category.setDescription(request.description());
        category.setActive(true);

        Category savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    // GET ALL CATEGORIES
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET CATEGORY BY ID
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {

        Category category = findCategory(id);

        return mapToResponse(category);
    }

    // UPDATE CATEGORY
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        Category category = findCategory(id);

        String name = request.name().trim();

        boolean duplicate =
                categoryRepository.existsByNameIgnoreCase(name);

        if (duplicate &&
                !category.getName().equalsIgnoreCase(name)) {

            throw new IllegalArgumentException(
                    "Category already exists"
            );
        }

        category.setName(name);
        category.setDescription(request.description());

        Category updatedCategory =
                categoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }

    // DELETE CATEGORY
    public void deleteCategory(Long id) {

        Category category = findCategory(id);

        categoryRepository.delete(category);
    }

    // FIND CATEGORY
    private Category findCategory(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with ID: " + id
                        )
                );
    }

    // ENTITY TO DTO MAPPING
    private CategoryResponse mapToResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
