package com.restro.service;

import com.restro.dto.request.AddToCartRequest;
import com.restro.dto.request.UpdateCartItemRequest;
import com.restro.dto.response.CartResponse;

public interface CartService {

    CartResponse addToCart(AddToCartRequest request);

    CartResponse getActiveCart(Long userId);

    CartResponse updateCartItem(UpdateCartItemRequest request);

    String removeCartItem(Long cartItemId);

    String clearCart(Long userId);
}