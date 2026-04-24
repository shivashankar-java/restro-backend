package com.restro.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.restro.dto.request.MenuRequest;
import com.restro.dto.response.MenuResponse;
import com.restro.entity.MenuItem;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    MenuItem toEntity(MenuRequest request);

    MenuResponse toResponse(MenuItem entity);

    List<MenuResponse> toResponseList(List<MenuItem> list);
}
