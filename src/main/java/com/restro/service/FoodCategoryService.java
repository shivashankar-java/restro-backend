package com.restro.service;

import com.restro.dto.request.FoodCategoryRequest;
import com.restro.dto.response.FoodCategoryResponse;

import java.util.List;
import java.util.UUID;

public interface FoodCategoryService {

    FoodCategoryResponse createCategory(FoodCategoryRequest request);

    FoodCategoryResponse getCategoryById(UUID categoryId);

    List<FoodCategoryResponse> getAllCategories();

}
