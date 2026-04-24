package com.restro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.response.MenuResponse;
import com.restro.service.MenuService;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // ✅ Add Menu (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MenuResponse> addMenu(@RequestBody MenuRequest request) {
        MenuResponse response = menuService.addMenu(request);
        return ResponseEntity.ok(response);
    }

    // ✅ Get All Menu (Public / All users)
    @GetMapping
    public ResponseEntity<List<MenuResponse>> getAllMenu() {
        return ResponseEntity.ok(menuService.getAllMenu());
    }

    // ✅ Update Menu (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MenuResponse> updateMenu(
            @PathVariable Long id,
            @RequestBody MenuRequest request) {

        return ResponseEntity.ok(menuService.updateMenu(id, request));
    }

    // ✅ Delete Menu (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenu(@PathVariable Long id) {

        menuService.deleteMenu(id);
        return ResponseEntity.ok("Menu item deleted successfully");
    }
    
}
