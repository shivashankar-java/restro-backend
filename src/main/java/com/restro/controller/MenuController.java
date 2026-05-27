package com.restro.controller;

import java.util.List;
import java.util.UUID;

import com.restro.dto.response.MenuDashboardResponse;
import com.restro.entity.FoodType;
import com.restro.entity.MenuStatus;
import com.restro.service.JwtService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.response.MenuResponse;
import com.restro.service.MenuService;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;
    private final JwtService jwtService;

    public MenuController(MenuService menuService, JwtService jwtService) {
        this.menuService = menuService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<MenuResponse> createMenu(@RequestBody MenuRequest request) {
        MenuResponse response = menuService.createMenu(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/getAll-pagination")
    public ResponseEntity<Page<MenuResponse>> getAllMenus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MenuResponse> response = menuService.getAllMenus(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuResponse> getMenuById(@PathVariable UUID menuId) {

        MenuResponse response = menuService.getMenuById(menuId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{menuId}")
    public ResponseEntity<MenuResponse> updateMenu(@PathVariable UUID menuId,
            @RequestBody MenuRequest request) {

        MenuResponse response = menuService.updateMenu(menuId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<String> deleteMenu(@PathVariable UUID menuId) {

        menuService.deleteMenu(menuId);
        return ResponseEntity.ok("Menu deleted successfully");
    }

    @GetMapping("/searchMenu")
    public ResponseEntity<Page<MenuResponse>> searchMenus(@RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MenuResponse> response = menuService.searchMenus(keyword, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filterByCategory")
    public ResponseEntity<Page<MenuResponse>> getMenusByCategory(@RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MenuResponse> response = menuService.getMenusByCategory(category, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filterByFood-type")
    public ResponseEntity<Page<MenuResponse>> getMenusByFoodType(@RequestParam FoodType foodType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MenuResponse> response = menuService.getMenusByFoodType(foodType, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Page<MenuResponse>> getMenusByStatus(@RequestParam MenuStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MenuResponse> response = menuService.getMenusByStatus(status, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/MenuDashboard")
    public ResponseEntity<MenuDashboardResponse> getDashboard() {

        MenuDashboardResponse response =menuService.getDashboard();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MenuResponse>> getAllMenus() {
        List<MenuResponse> menus = menuService.getAllMenus();
        return ResponseEntity.ok(menus);
    }

}
    

