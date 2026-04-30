package com.restro.service.impl;

import com.restro.dto.request.RestaurantRequest;
import com.restro.dto.response.RestaurantResponse;
import com.restro.entity.MenuItem;
import com.restro.entity.Restaurant;
import com.restro.mapper.RestaurantMapper;
import com.restro.repository.MenuItemRepository;
import com.restro.repository.RestaurantRepository;
import com.restro.service.RestaurantService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuRepository;
    private final RestaurantMapper restaurantMapper;

    public RestaurantServiceImpl(
            RestaurantRepository restaurantRepository,
            MenuItemRepository menuRepository,
            RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.restaurantMapper = restaurantMapper;
    }

    // ADMIN → Add Restaurant
    @Override
    public RestaurantResponse addRestaurant(RestaurantRequest request) {

        Restaurant restaurant = restaurantMapper.toEntity(request);

        List<MenuItem> menuItems = menuRepository.findAllById(request.getMenuIds());

        restaurant.setMenuItems(menuItems);

        Restaurant saved = restaurantRepository.save(restaurant);

        return restaurantMapper.toResponse(saved);
    }

    // CUSTOMER → Get Restaurants by Menu Item
    @Override
    public List<RestaurantResponse> getRestaurantsByMenu(Long menuId) {

        MenuItem menuItem = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        List<Restaurant> restaurants =
                restaurantRepository.findByMenuItemsContaining(menuItem);

        return restaurants.stream()
                .map(restaurantMapper::toResponse)
                .toList();
    }

    // Get All Restaurants
    @Override
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurantMapper::toResponse)
                .toList();
    }
}