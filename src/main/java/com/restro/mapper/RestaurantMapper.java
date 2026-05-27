package com.restro.mapper;

import com.restro.dto.request.RestaurantAdminRequest;
import com.restro.dto.request.RestaurantRequest;
import com.restro.dto.response.RestaurantResponse;
import com.restro.entity.MenuItem;
import com.restro.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    Restaurant toEntity(RestaurantAdminRequest request);

    RestaurantResponse toResponse(Restaurant restaurant);

    void updateRestaurantFromRequest(
            RestaurantRequest request,
            @MappingTarget Restaurant restaurant
    );
}
