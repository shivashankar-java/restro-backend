package com.restro.service.impl;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.request.MenuRequest1;
import com.restro.dto.request.RestaurantAdminRequest;
import com.restro.dto.request.RestaurantRequest;
import com.restro.dto.response.RestaurantResponse;
import com.restro.entity.FoodCategory;
import com.restro.entity.MenuItem;
import com.restro.entity.Restaurant;
import com.restro.mapper.RestaurantMapper;
import com.restro.repository.FoodCategoryRepository;
import com.restro.repository.MenuItemRepository;
import com.restro.repository.RestaurantRepository;
import com.restro.service.RestaurantService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final PasswordEncoder passwordEncoder;

    public RestaurantServiceImpl(
            RestaurantRepository restaurantRepository,
            RestaurantMapper restaurantMapper,
            PasswordEncoder passwordEncoder) {

        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RestaurantResponse createRestaurant(RestaurantAdminRequest request) {

        // CHECK EMAIL EXISTS
        if (restaurantRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Restaurant email already exists");
        }

        String tempPassword = generateTempPassword();
        Restaurant restaurant = restaurantMapper.toEntity(request);
        restaurant.setPassword(passwordEncoder.encode(tempPassword));
        restaurant.setActive(true);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        RestaurantResponse response = new RestaurantResponse();

        response.setRestaurantId(savedRestaurant.getId());
        response.setRestaurantName(savedRestaurant.getRestaurantName());
        response.setEmail(savedRestaurant.getEmail());
        response.setPhone(savedRestaurant.getPhone());
        response.setCity(savedRestaurant.getCity());
        response.setState(savedRestaurant.getState());
        response.setActive(savedRestaurant.getActive());

        response.setOwnerName(request.getOwnerName());
        response.setOwnerEmail(request.getOwnerEmail());
        response.setTemporaryPassword(tempPassword);
        return response;
    }



    private String generateTempPassword() {

        return "RESTRO@" + UUID.randomUUID()
                        .toString()
                        .substring(0, 5);
    }

    // UPDATE
    @Override
    public RestaurantResponse updateRestaurant(UUID restaurantId, RestaurantRequest request) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        restaurantMapper.updateRestaurantFromRequest(request, restaurant);
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toResponse(updatedRestaurant);
    }

    // GET BY ID
    @Override
    public RestaurantResponse getRestaurantById(UUID restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return restaurantMapper.toResponse(restaurant);
    }

    // GET ALL
    @Override
    public List<RestaurantResponse> getAllRestaurants() {

        return restaurantRepository.findAll()
                .stream()
                .map(restaurantMapper::toResponse)
                .toList();
    }

    // DELETE
    @Override
    public void deleteRestaurant(UUID restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        restaurantRepository.delete(restaurant);
    }

}