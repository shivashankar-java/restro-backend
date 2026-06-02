package com.restro.service;

import com.restro.dto.request.AddToCartRequest;
import com.restro.dto.request.ApplyCouponRequest;
import com.restro.dto.request.UpdateCartItemRequest;
import com.restro.dto.response.CartResponse;

import java.util.UUID;

public interface CartService {

    CartResponse getCart(UUID cartId);

    CartResponse addItem(AddToCartRequest request);

    CartResponse updateItem(UUID cartItemId, Integer quantity);

    void removeItem(UUID cartItemId);

    void clearCart(UUID cartId);
}