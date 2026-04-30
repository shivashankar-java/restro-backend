package com.restro.mapper;

import com.restro.dto.response.CartItemResponse;
import com.restro.dto.response.CartResponse;
import com.restro.entity.Cart;
import com.restro.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "cartItemId", source = "cartItem.id")
    @Mapping(target = "menuId", source = "cartItem.menuItem.id")
    @Mapping(target = "menuName", source = "cartItem.menuItem.name")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    @Mapping(target = "cartId", source = "cart.id")
    @Mapping(target = "restaurantName", source = "cart.restaurant.name")
    @Mapping(target = "items", source = "cart.cartItems")
    @Mapping(target = "status", source = "cart.status")
    CartResponse toCartResponse(Cart cart);
}