package com.restro.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.response.MenuResponse;
import com.restro.entity.MenuItem;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    MenuItem toEntity(MenuRequest request);

    @Mapping(source = "category.categoryName", target = "category")
    MenuResponse toResponse(MenuItem item);

    List<MenuResponse> toResponseList(List<MenuItem> items);

}
