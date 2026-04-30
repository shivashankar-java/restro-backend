package com.restro.service;

import com.restro.dto.request.AddToCartRequest;
import com.restro.dto.request.UpdateCartItemRequest;
import com.restro.dto.response.CartResponse;

public interface CartService {

    CartResponse addToCart(AddToCartRequest request);

    CartResponse getActiveCart();

    CartResponse updateItemQuantity(UpdateCartItemRequest request);

    CartResponse removeItem(Long cartItemId);

    String clearCart();
}