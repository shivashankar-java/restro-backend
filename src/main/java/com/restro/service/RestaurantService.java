package com.restro.service;

import com.restro.dto.request.RestaurantRequest;
import com.restro.dto.response.RestaurantResponse;

import java.util.List;

public interface RestaurantService {

    RestaurantResponse addRestaurant(RestaurantRequest request);

    List<RestaurantResponse> getRestaurantsByMenu(Long menuId);

    List<RestaurantResponse> getAllRestaurants();
}
