package com.restro.service;

import java.util.List;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.response.MenuResponse;
import com.restro.entity.Category;

public interface MenuService {

    MenuResponse addMenu(MenuRequest request);

    List<MenuResponse> getAllMenu();

    MenuResponse updateMenu(Long id, MenuRequest request);

    void deleteMenu(Long id);

    List<MenuResponse> getMenuByCategory(Category category);
}