package com.restro.service.impl;

import java.util.List;
import java.util.UUID;

import com.restro.dto.response.MenuDashboardResponse;
import com.restro.entity.*;
import com.restro.exceptions.DuplicateResourceException;
import com.restro.repository.FoodCategoryRepository;
import com.restro.repository.RestaurantRepository;
import com.restro.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.response.MenuResponse;
import com.restro.mapper.MenuMapper;
import com.restro.repository.MenuItemRepository;
import com.restro.service.MenuService;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepository menuRepository;
    private final MenuMapper menuMapper;
    private final FoodCategoryRepository foodCategoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public MenuServiceImpl(MenuItemRepository menuRepository, MenuMapper menuMapper, FoodCategoryRepository foodCategoryRepository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
		super();
		this.menuRepository = menuRepository;
		this.menuMapper = menuMapper;
        this.foodCategoryRepository = foodCategoryRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    // GET LOGGED-IN RESTAURANT
    private Restaurant getLoggedInRestaurant() {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getRestaurant();
    }

    // CREATE MENU
    @Override
    public MenuResponse createMenu(MenuRequest request) {

        Restaurant restaurant = getLoggedInRestaurant();

        // CHECK DUPLICATE MENU
        boolean exists = menuRepository.findByRestaurantIdAndItemNameIgnoreCase(
                        restaurant.getId(),
                        request.getItemName()).isPresent();

        if (exists) {
            throw new DuplicateResourceException("Menu already exists");
        }

        FoodCategory category = foodCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        MenuItem menuItem = menuMapper.toEntity(request);
        menuItem.setRestaurant(restaurant);
        menuItem.setCategory(category);
        menuRepository.save(menuItem);
        return menuMapper.toResponse(menuItem);
    }

    // UPDATE MENU
    @Override
    public MenuResponse updateMenu(UUID menuId, MenuRequest request) {

        Restaurant restaurant = getLoggedInRestaurant();

        MenuItem menuItem = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));

        // SECURITY CHECK
        if (!menuItem.getRestaurant().getId()
                .equals(restaurant.getId())) {

            throw new RuntimeException("You cannot update another restaurant menu");
        }

        FoodCategory category =
                foodCategoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));

        menuItem.setItemName(request.getItemName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setPreparationTime(request.getPreparationTime());
        menuItem.setFoodType(request.getFoodType());
        menuItem.setStatus(request.getStatus());
        menuItem.setImageUrl(request.getImageUrl());

        menuItem.setCategory(category);
        menuRepository.save(menuItem);

        return menuMapper.toResponse(menuItem);
    }

    // DELETE MENU
    @Override
    public void deleteMenu(UUID menuId) {

        Restaurant restaurant = getLoggedInRestaurant();

        MenuItem menuItem = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));

        // SECURITY CHECK
        if (!menuItem.getRestaurant().getId()
                .equals(restaurant.getId())) {

            throw new RuntimeException("You cannot delete another restaurant menu");
        }

        menuRepository.delete(menuItem);
    }

    // GET MENU BY ID
    @Override
    public MenuResponse getMenuById(UUID menuId) {

        MenuItem menuItem = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));
        return menuMapper.toResponse(menuItem);
    }

    // GET ALL MENUS
    @Override
    public Page<MenuResponse> getAllMenus(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return menuRepository.findAll(pageable)
                .map(menuMapper::toResponse);
    }

    // SEARCH MENUS
    @Override
    public Page<MenuResponse> searchMenus(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return menuRepository.findByItemNameContainingIgnoreCase(
                        keyword,
                        pageable)
                .map(menuMapper::toResponse);
    }

    // FILTER BY CATEGORY
    @Override
    public Page<MenuResponse> getMenusByCategory(String category, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return menuRepository.findByCategory_CategoryNameIgnoreCase(category, pageable)
                .map(menuMapper::toResponse);
    }

    // FILTER BY FOOD TYPE
    @Override
    public Page<MenuResponse> getMenusByFoodType(FoodType foodType, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return menuRepository.findByFoodType(foodType, pageable)
                .map(menuMapper::toResponse);
    }

    // FILTER BY STATUS
    @Override
    public Page<MenuResponse> getMenusByStatus(MenuStatus status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return menuRepository.findByStatus(status, pageable)
                .map(menuMapper::toResponse);
    }


    @Override
    public MenuDashboardResponse getDashboard() {

        Restaurant restaurant = getLoggedInRestaurant();
        UUID restaurantId = restaurant.getId();

        long totalItems = menuRepository.countByRestaurantId(restaurantId);
        long vegItems = menuRepository.countByRestaurantIdAndFoodType(restaurantId, FoodType.VEG);
        long nonVegItems = menuRepository.countByRestaurantIdAndFoodType(restaurantId, FoodType.NON_VEG);
        long availableItems = menuRepository.countByRestaurantIdAndStatus(restaurantId, MenuStatus.AVAILABLE);
        long outOfStockItems = menuRepository.countByRestaurantIdAndStatus(restaurantId, MenuStatus.OUT_OF_STOCK);
        long categoryCount = foodCategoryRepository.count();

        Double avgPrice = menuRepository.getAveragePriceByRestaurant(restaurantId);

        if (avgPrice == null) {
            avgPrice = 0.0;
        }

        MenuDashboardResponse response = new MenuDashboardResponse();

        response.setTotalItems(totalItems);
        response.setVegItems(vegItems);
        response.setNonVegItems(nonVegItems);
        response.setAvailableItems(availableItems);
        response.setOutOfStockItems(outOfStockItems);
        response.setCategoryCount(categoryCount);
        response.setAveragePrice(avgPrice);

        return response;
    }

    // GET ALL MENUS WITHOUT PAGINATION
    @Override
    public List<MenuResponse> getAllMenus() {

        return menuRepository.findAll()
                .stream()
                .map(menuMapper::toResponse)
                .toList();
    }

}
