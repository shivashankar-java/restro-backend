package com.restro.service.impl;

import com.restro.dto.request.FoodCategoryRequest;
import com.restro.dto.response.FoodCategoryResponse;
import com.restro.entity.FoodCategory;
import com.restro.entity.Restaurant;
import com.restro.mapper.FoodCategoryMapper;
import com.restro.repository.FoodCategoryRepository;
import com.restro.repository.RestaurantRepository;
import com.restro.service.FoodCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FoodCategoryServiceImpl implements FoodCategoryService {

    private final FoodCategoryRepository foodCategoryRepository;
    private final FoodCategoryMapper foodCategoryMapper;
    private final RestaurantRepository restaurantRepository;

    public FoodCategoryServiceImpl(FoodCategoryRepository foodCategoryRepository, FoodCategoryMapper foodCategoryMapper, RestaurantRepository restaurantRepository) {
        this.foodCategoryRepository = foodCategoryRepository;
        this.foodCategoryMapper = foodCategoryMapper;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public FoodCategoryResponse createCategory(FoodCategoryRequest request) {

        FoodCategory category = new FoodCategory();

        category.setCategoryName(request.getCategoryName());
        category.setCategoryImageUrl(request.getCategoryImageUrl());

        FoodCategory savedCategory = foodCategoryRepository.save(category);

        return foodCategoryMapper.toResponse(savedCategory);
    }

    @Override
    public FoodCategoryResponse getCategoryById(UUID categoryId) {

        FoodCategory category = foodCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return foodCategoryMapper.toResponse(category);
    }

    @Override
    public List<FoodCategoryResponse> getAllCategories() {

        return foodCategoryRepository.findAll()
                .stream()
                .map(foodCategoryMapper::toResponse)
                .toList();
    }
}
