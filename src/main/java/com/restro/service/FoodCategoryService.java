package com.restro.service;

import com.restro.dto.request.FoodCategoryRequest;
import com.restro.dto.response.FoodCategoryResponse;
import com.restro.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface FoodCategoryService {

    FoodCategoryResponse addCategory(FoodCategoryRequest request);

    FoodCategoryResponse updateCategory(UUID categoryId, FoodCategoryRequest request);

    void deleteCategory(UUID categoryId);

    FoodCategoryResponse getCategoryById(UUID categoryId);

    List<FoodCategoryResponse> getAllCategories();

    Page<FoodCategoryResponse> getAllCategories(Pageable pageable);

    List<FoodCategoryResponse> searchCategories(String keyword);

    FoodCategoryResponse updateCategoryStatus(UUID categoryId, Status status);
}
