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
    public ResponseEntity<CartResponse> addToCart(@RequestBody AddToCartRequest request) {

        return ResponseEntity.ok(
                cartService.addToCart(request)
        );
    }

    // CUSTOMER only → Get active cart
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getActiveCart(@PathVariable Long userId) {

        return ResponseEntity.ok(
                cartService.getActiveCart(userId)
        );
    }

    // CUSTOMER only → Update cart item quantity
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/update")
    public ResponseEntity<CartResponse> updateCartItem(@RequestBody UpdateCartItemRequest request) {

        return ResponseEntity.ok(
                cartService.updateCartItem(request)
        );
    }

    // CUSTOMER only → Remove single cart item
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeCartItem(@PathVariable Long cartItemId) {

        return ResponseEntity.ok(
                cartService.removeCartItem(cartItemId)
        );
    }

    // CUSTOMER only → Clear full cart
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable Long userId) {

        return ResponseEntity.ok(
                cartService.clearCart(userId)
        );
    }
}
