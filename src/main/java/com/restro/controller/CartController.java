package com.restro.controller;

import com.restro.dto.request.AddToCartRequest;
import com.restro.dto.request.UpdateCartItemRequest;
import com.restro.dto.response.CartResponse;
import com.restro.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // CUSTOMER only → Add item to cart
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    // CUSTOMER only → Get active cart
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public ResponseEntity<CartResponse> getActiveCart() {

        return ResponseEntity.ok(
                cartService.getActiveCart()
        );
    }

    // UPDATE CART ITEM QTY
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/item")
    public ResponseEntity<CartResponse> updateCartItem(
            @RequestBody UpdateCartItemRequest request) {

        return ResponseEntity.ok(
                cartService.updateItemQuantity(request)
        );
    }

    // REMOVE SINGLE ITEM
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<CartResponse> removeCartItem(@PathVariable Long cartItemId) {

        return ResponseEntity.ok(
                cartService.removeItem(cartItemId)
        );
    }

    // CLEAR CART
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        return ResponseEntity.ok(
                cartService.clearCart()
        );
    }
}
