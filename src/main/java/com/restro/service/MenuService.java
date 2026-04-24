package com.restro.service;

import java.util.List;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.response.MenuResponse;

public interface MenuService {

    MenuResponse addMenu(MenuRequest request);

    List<MenuResponse> getAllMenu();

    MenuResponse updateMenu(Long id, MenuRequest request);

    void deleteMenu(Long id);
}