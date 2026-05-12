package com.restro.controller;

import com.restro.dto.request.FoodCategoryRequest;
import com.restro.dto.response.FoodCategoryResponse;
import com.restro.service.FoodCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class FoodCategoryController {

    private final FoodCategoryService foodCategoryService;

    public FoodCategoryController(FoodCategoryService foodCategoryService) {
        this.foodCategoryService = foodCategoryService;
    }

    @PostMapping
    public ResponseEntity<FoodCategoryResponse> createCategory(
            @RequestBody FoodCategoryRequest request) {

        FoodCategoryResponse response =
                foodCategoryService.createCategory(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<FoodCategoryResponse> getCategoryById(
            @PathVariable UUID categoryId) {

        FoodCategoryResponse response =
                foodCategoryService.getCategoryById(categoryId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FoodCategoryResponse>> getAllCategories() {

        List<FoodCategoryResponse> response =
                foodCategoryService.getAllCategories();

        return ResponseEntity.ok(response);
    }
}
