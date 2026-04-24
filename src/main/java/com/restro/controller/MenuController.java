package com.restro.controller;

import java.util.List;

import com.restro.entity.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.response.MenuResponse;
import com.restro.service.MenuService;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    //  Add Menu (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MenuResponse> addMenu(@RequestBody MenuRequest request) {
        MenuResponse response = menuService.addMenu(request);
        return ResponseEntity.ok(response);
    }

    //  Get All Menu (Public / All users)
    @GetMapping
    public ResponseEntity<List<MenuResponse>> getAllMenu() {
        return ResponseEntity.ok(menuService.getAllMenu());
    }

    //  Update Menu (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MenuResponse> updateMenu(
            @PathVariable Long id,
            @RequestBody MenuRequest request) {

        return ResponseEntity.ok(menuService.updateMenu(id, request));
    }

    //  Delete Menu (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenu(@PathVariable Long id) {

        menuService.deleteMenu(id);
        return ResponseEntity.ok("Menu item deleted successfully");
    }

    @GetMapping("/category")
    public ResponseEntity<List<MenuResponse>> getMenuByCategory(
            @RequestParam Category category) {

        return ResponseEntity.ok(menuService.getMenuByCategory(category));
    }
    
}
