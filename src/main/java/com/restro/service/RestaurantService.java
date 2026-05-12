package com.restro.service;

import com.restro.dto.request.RestaurantRequest;
import com.restro.dto.response.RestaurantResponse;

import java.util.List;
import java.util.UUID;

public interface RestaurantService {

    RestaurantResponse addRestaurant(RestaurantRequest request);

    List<RestaurantResponse> getRestaurantsByMenu(UUID menuId);

    List<RestaurantResponse> getAllRestaurants();
}
