package com.restro.mapper;

import com.restro.dto.response.CartItemResponse;
import com.restro.dto.response.CartResponse;
import com.restro.entity.Cart;
import com.restro.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "id", target = "cartId")
    @Mapping(source = "restaurant.id", target = "restaurantId")
    @Mapping(source = "cartItems", target = "items")
    CartResponse toResponse(Cart cart);

    @Mapping(source = "id", target = "cartItemId")
    @Mapping(source = "menuItem.id", target = "menuId")
    @Mapping(source = "menuItem.itemName", target = "menuName")
    CartItemResponse toItemResponse(CartItem item);

}