package com.restro.service;

import java.util.List;
import java.util.UUID;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.response.MenuDashboardResponse;
import com.restro.dto.response.MenuResponse;
import com.restro.entity.Category;
import com.restro.entity.FoodType;
import com.restro.entity.MenuStatus;
import org.springframework.data.domain.Page;

public interface MenuService {

    MenuResponse createMenu(MenuRequest request);
    MenuResponse updateMenu(UUID menuId, MenuRequest request);
    void deleteMenu(UUID menuId);
    MenuResponse getMenuById(UUID menuId);
    Page<MenuResponse> getAllMenus(int page, int size);
    Page<MenuResponse> searchMenus(String keyword, int page, int size);
    Page<MenuResponse> getMenusByCategory(String category, int page, int size);
    Page<MenuResponse> getMenusByFoodType(FoodType foodType, int page, int size);
    Page<MenuResponse> getMenusByStatus(MenuStatus status, int page, int size);
    MenuDashboardResponse getDashboard();
    List<MenuResponse> getAllMenus();



}