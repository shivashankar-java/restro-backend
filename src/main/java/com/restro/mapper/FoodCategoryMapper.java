package com.restro.mapper;

import com.restro.dto.request.FoodCategoryRequest;
import com.restro.dto.response.FoodCategoryResponse;
import com.restro.dto.response.RestaurantResponse;
import com.restro.entity.FoodCategory;
import com.restro.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FoodCategoryMapper {

    FoodCategory toEntity(FoodCategoryRequest request);

    @Mapping(source = "id", target = "restaurantId")
    RestaurantResponse toRestaurantResponse(Restaurant restaurant);

    List<RestaurantResponse> toRestaurantResponseList(List<Restaurant> restaurants);

    FoodCategoryResponse toResponse(FoodCategory category);
}