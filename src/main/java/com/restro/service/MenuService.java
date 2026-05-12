package com.restro.service;

import java.util.List;
import java.util.UUID;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.response.MenuResponse;
import com.restro.entity.Category;

public interface MenuService {

    MenuResponse addMenu(MenuRequest request);

    List<MenuResponse> getAllMenu();

    MenuResponse updateMenu(UUID id, MenuRequest request);

    void deleteMenu(UUID id);

    List<MenuResponse> getMenuByCategory(String categoryName);

}