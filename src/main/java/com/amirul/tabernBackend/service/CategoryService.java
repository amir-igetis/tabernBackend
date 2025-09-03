package com.amirul.tabernBackend.service;

import com.amirul.tabernBackend.dtos.CategoryResponseDTO;
import com.amirul.tabernBackend.dtos.CategorySimpleDTO;
import com.amirul.tabernBackend.dtos.CreateCategoryRequestDTO;
import com.amirul.tabernBackend.dtos.UpdateCategoryRequestDTO;
import com.amirul.tabernBackend.model.Category;
import com.amirul.tabernBackend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDTO createCategory(CreateCategoryRequestDTO request) throws IllegalAccessException {
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalAccessException("Category with name '" + request.getName() + "' already exists");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        category.setActive(request.getActive() != null ? request.getActive() : true);

        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceAccessException("Parent category not found with id: " + request.getParentCategoryId()));
            category.setParentCategory(parentCategory);
        }

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
//        categoryRepository.findAllByParentCategoryIsNullOrderByDisplayOrderAsc();
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceAccessException("Category not found with id: " + id));
        return mapToResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getActiveCategories() {
        List<Category> categories = categoryRepository.findByActiveTrueAndParentCategoryIsNullOrderByDisplayOrderAsc();
//        categoryRepository.findByActiveTrueAndParentCategoryIsNullOrderByDisplayOrderAsc();
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, UpdateCategoryRequestDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceAccessException("Category not found with id: " + id));

        // Check if name is being changed and if new name already exists
        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : category.getDisplayOrder());
        category.setActive(request.getActive() != null ? request.getActive() : category.getActive());

        // Update parent category if provided
        if (request.getParentCategoryId() != null) {
            if (request.getParentCategoryId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }

            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceAccessException("Parent category not found with id: " + request.getParentCategoryId()));

            // Check for circular reference
            if (isCircularReference(parentCategory, id)) {
                throw new IllegalArgumentException("Circular reference detected in category hierarchy");
            }

            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceAccessException("Category not found with id: " + id));

        // Check if category has products
        if (!category.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with associated products");
        }

        // Handle subcategories - either delete them or set their parent to null
        if (!category.getSubCategories().isEmpty()) {
            // Option 1: Delete subcategories (cascade)
            // categoryRepository.deleteAll(category.getSubCategories());

            // Option 2: Set parent to null for subcategories
            for (Category subCategory : category.getSubCategories()) {
                subCategory.setParentCategory(null);
            }
            categoryRepository.saveAll(category.getSubCategories());
        }

        categoryRepository.delete(category);
    }


    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getSubCategories(Long parentId) {
        Category parentCategory = categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceAccessException("Parent category not found with id: " + parentId));

        return parentCategory.getSubCategories().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> searchCategories(String name) {
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(name);
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private boolean isCircularReference(Category potentialParent, Long currentCategoryId) {
        Category current = potentialParent;
        while (current != null) {
            if (current.getId().equals(currentCategoryId)) {
                return true;
            }
            current = current.getParentCategory();
        }
        return false;
    }

    private CategoryResponseDTO mapToResponse(Category category) {
        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setImageUrl(category.getImageUrl());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setActive(category.getActive());
        response.setProductCount(category.getProducts() != null ? category.getProducts().size() : 0);

        if (category.getParentCategory() != null) {
            CategorySimpleDTO parentDto = new CategorySimpleDTO();
            parentDto.setId(category.getParentCategory().getId());
            parentDto.setName(category.getParentCategory().getName());
            parentDto.setImageUrl(category.getParentCategory().getImageUrl());
            parentDto.setDisplayOrder(category.getParentCategory().getDisplayOrder());
            response.setParentCategory(parentDto);
        }

        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            List<CategorySimpleDTO> subCategoryDtos = category.getSubCategories().stream()
                    .map(sub -> {
                        CategorySimpleDTO dto = new CategorySimpleDTO();
                        dto.setId(sub.getId());
                        dto.setName(sub.getName());
                        dto.setImageUrl(sub.getImageUrl());
                        dto.setDisplayOrder(sub.getDisplayOrder());
                        return dto;
                    })
                    .collect(Collectors.toList());
            response.setSubCategories(subCategoryDtos);
        }

        return response;
    }
}
