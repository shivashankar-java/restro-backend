package com.restro.service.impl;

import java.util.List;
import java.util.UUID;

import com.restro.entity.Category;
import com.restro.entity.FoodCategory;
import com.restro.repository.FoodCategoryRepository;
import org.springframework.stereotype.Service;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.response.MenuResponse;
import com.restro.entity.MenuItem;
import com.restro.mapper.MenuMapper;
import com.restro.repository.MenuItemRepository;
import com.restro.service.MenuService;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepository menuRepository;
    private final MenuMapper menuMapper;
    private final FoodCategoryRepository foodCategoryRepository;

    public MenuServiceImpl(MenuItemRepository menuRepository, MenuMapper menuMapper, FoodCategoryRepository foodCategoryRepository) {
		super();
		this.menuRepository = menuRepository;
		this.menuMapper = menuMapper;
        this.foodCategoryRepository = foodCategoryRepository;
    }

    @Override
    public MenuResponse updateMenu(UUID id, MenuRequest request) {

        MenuItem item = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        FoodCategory category = foodCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setAvailable(request.getAvailable());
        item.setRating(request.getRating());

        item.setCategory(category);

        return menuMapper.toResponse(menuRepository.save(item));
    }

    //  Get All Menu
    @Override
    public List<MenuResponse> getAllMenu() {
        return menuMapper.toResponseList(menuRepository.findAll());
    }

    //  Update Menu
    @Override
    public MenuResponse addMenu(MenuRequest request) {

        FoodCategory category = foodCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        MenuItem item = menuMapper.toEntity(request);

        item.setCategory(category);

        return menuMapper.toResponse(
                menuRepository.save(item));
    }

    //  Delete Menu
    @Override
    public void deleteMenu(UUID id) {
    	menuRepository.deleteById(id);
    }

    @Override
    public List<MenuResponse> getMenuByCategory(String categoryName) {

        List<MenuItem> items = menuRepository.findByCategory_CategoryName(categoryName);

        return menuMapper.toResponseList(items);
    }

}
