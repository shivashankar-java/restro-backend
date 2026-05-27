package com.restro.service;

import com.restro.dto.request.RestaurantAdminRequest;
import com.restro.dto.request.RestaurantRequest;
import com.restro.dto.response.RestaurantResponse;

import java.util.List;
import java.util.UUID;

public interface RestaurantService {

    RestaurantResponse createRestaurant(RestaurantAdminRequest request);

    RestaurantResponse updateRestaurant(UUID restaurantId,
                                        RestaurantRequest request);

    RestaurantResponse getRestaurantById(UUID restaurantId);

    List<RestaurantResponse> getAllRestaurants();

    void deleteRestaurant(UUID restaurantId);
}
