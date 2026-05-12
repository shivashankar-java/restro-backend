package com.restro.mapper;

import com.restro.dto.request.RestaurantRequest;
import com.restro.dto.response.RestaurantResponse;
import com.restro.entity.MenuItem;
import com.restro.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    Restaurant toEntity(RestaurantRequest request);

    @Mapping(target = "menuNames", source = "menuItems")
    RestaurantResponse toResponse(Restaurant restaurant);

    List<RestaurantResponse> toResponseList(List<Restaurant> restaurants);

    default List<String> mapMenuItems(List<MenuItem> menuItems) {
        return menuItems.stream()
                .map(MenuItem::getName)
                .toList();
    }
}
