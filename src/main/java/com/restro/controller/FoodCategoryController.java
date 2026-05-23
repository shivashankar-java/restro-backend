package com.restro.controller;

import com.restro.dto.request.FoodCategoryRequest;
import com.restro.dto.response.FoodCategoryResponse;
import com.restro.entity.Status;
import com.restro.service.FoodCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping("/{categoryId}")
    public ResponseEntity<FoodCategoryResponse> getCategoryById(@PathVariable UUID categoryId) {

        FoodCategoryResponse response = foodCategoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FoodCategoryResponse>> getAllCategories() {

        List<FoodCategoryResponse> response = foodCategoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public FoodCategoryResponse addCategory(@RequestBody FoodCategoryRequest request) {
        return foodCategoryService.addCategory(request);
    }

    @PutMapping("/{categoryId}")
    public FoodCategoryResponse updateCategory(@PathVariable UUID categoryId,
                                               @RequestBody FoodCategoryRequest request) {
        return foodCategoryService.updateCategory(categoryId, request);
    }

    @DeleteMapping("/{categoryId}")
    public String deleteCategory(@PathVariable UUID categoryId) {

        foodCategoryService.deleteCategory(categoryId);
        return "Food category deleted successfully";
    }

    @GetMapping("/searchCategory")
    public List<FoodCategoryResponse> searchCategories(@RequestParam String keyword) {
        return foodCategoryService.searchCategories(keyword);
    }

    @GetMapping("/pagination")
    public Page<FoodCategoryResponse> getAllCategories(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "categoryName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return foodCategoryService.getAllCategories(pageable);
    }

    @PutMapping("/{categoryId}/status")
    public ResponseEntity<FoodCategoryResponse> updateCategoryStatus(
            @PathVariable UUID categoryId,
            @RequestParam Status status) {

        FoodCategoryResponse response =
                foodCategoryService.updateCategoryStatus(categoryId, status);

        return ResponseEntity.ok(response);
    }

}
