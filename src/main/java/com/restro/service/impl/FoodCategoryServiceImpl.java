package com.restro.service.impl;

import com.restro.dto.request.FoodCategoryRequest;
import com.restro.dto.response.FoodCategoryResponse;
import com.restro.entity.FoodCategory;
import com.restro.entity.Status;
import com.restro.mapper.FoodCategoryMapper;
import com.restro.repository.FoodCategoryRepository;
import com.restro.repository.RestaurantRepository;
import com.restro.service.FoodCategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public FoodCategoryResponse addCategory(FoodCategoryRequest request) {

        FoodCategory category = foodCategoryMapper.toEntity(request);

        FoodCategory savedCategory =
                foodCategoryRepository.save(category);

        return foodCategoryMapper.toResponse(savedCategory);
    }

    @Override
    public FoodCategoryResponse updateCategory(UUID categoryId,
                                               FoodCategoryRequest request) {

        FoodCategory category = foodCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        category.setCategoryName(request.getCategoryName());
        category.setCategoryImageUrl(request.getCategoryImageUrl());
        category.setDescription(request.getDescription());
        category.setStatus(request.getStatus());

        FoodCategory updatedCategory =
                foodCategoryRepository.save(category);

        return foodCategoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID categoryId) {

        FoodCategory category = foodCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        foodCategoryRepository.delete(category);
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


    @Override
    public Page<FoodCategoryResponse> getAllCategories(Pageable pageable) {

        Page<FoodCategory> categories =
                foodCategoryRepository.findAll(pageable);

        return categories.map(foodCategoryMapper::toResponse);
    }


    @Override
    public List<FoodCategoryResponse> searchCategories(String keyword) {

        List<FoodCategory> categories =
                foodCategoryRepository
                        .findByCategoryNameContainingIgnoreCase(keyword);

        return categories.stream()
                .map(foodCategoryMapper::toResponse)
                .toList();
    }

    @Override
    public FoodCategoryResponse updateCategoryStatus(UUID categoryId,
                                                     Status status) {

        FoodCategory category = foodCategoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        category.setStatus(status);

        FoodCategory updatedCategory =
                foodCategoryRepository.save(category);

        return foodCategoryMapper.toResponse(updatedCategory);
    }

}
