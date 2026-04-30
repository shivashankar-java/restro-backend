package com.restro.mapper;

import com.restro.dto.response.OrderItemResponse;
import com.restro.dto.response.OrderResponse;
import com.restro.entity.Order;
import com.restro.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderItemId", source = "id")
    @Mapping(target = "menuId", source = "menuItem.id")
    @Mapping(target = "menuName", source = "menuItem.name")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "restaurantName", source = "restaurant.name")
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "status", source = "status")
    OrderResponse toOrderResponse(Order order);
}
