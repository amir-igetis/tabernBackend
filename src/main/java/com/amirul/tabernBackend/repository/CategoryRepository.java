package com.amirul.tabernBackend.repository;

import com.amirul.tabernBackend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    List<Category> findByActiveTrueAndParentCategoryIsNullOrderByDisplayOrderAsc();

    List<Category> findAllByParentCategoryIsNullOrderByDisplayOrderAsc();

    List<Category> findByParentCategoryId(Long parentId);

    List<Category> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL ORDER BY c.displayOrder ASC")
    List<Category> findRootCategories();

    Optional<Category> findByName(String name);
}
