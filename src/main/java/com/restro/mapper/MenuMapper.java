package com.restro.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.response.MenuResponse;
import com.restro.entity.MenuItem;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    List<MenuResponse> toResponseList(List<MenuItem> items);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    MenuItem toEntity(MenuRequest request);

    @Mapping(source = "id", target = "menuId")
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.categoryName", target = "categoryName")
    MenuResponse toResponse(MenuItem menuItem);

}
