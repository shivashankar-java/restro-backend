package com.restro.mapper;

import com.restro.dto.request.RestaurantRequest;
import com.restro.dto.response.RestaurantResponse;
import com.restro.entity.Restaurant;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    Restaurant toEntity(RestaurantRequest request);

    default RestaurantResponse toResponse(Restaurant restaurant) {
        RestaurantResponse response = new RestaurantResponse();

        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setAddress(restaurant.getAddress());
        response.setPhone(restaurant.getPhone());
        response.setEmail(restaurant.getEmail());

        List<String> menuNames = restaurant.getMenuItems()
                .stream()
                .map(menu -> menu.getName())
                .collect(Collectors.toList());

        response.setMenuNames(menuNames);

        return response;
    }

    List<RestaurantResponse> toResponseList(List<Restaurant> restaurants);
}
