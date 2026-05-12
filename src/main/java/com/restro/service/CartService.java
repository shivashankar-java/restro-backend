//package com.restro.service;
//
//import com.restro.dto.request.AddToCartRequest;
//import com.restro.dto.request.UpdateCartItemRequest;
//import com.restro.dto.response.CartResponse;
//
//import java.util.UUID;
//
//public interface CartService {
//
//    CartResponse addToCart(AddToCartRequest request);
//
//    CartResponse getActiveCart();
//
//    CartResponse updateItemQuantity(UpdateCartItemRequest request);
//
//    CartResponse removeItem(UUID cartItemId);
//
//    String clearCart();
//}