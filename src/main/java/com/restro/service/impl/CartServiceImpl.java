package com.restro.service.impl;

import com.restro.config.JwtUtil;
import com.restro.dto.request.AddToCartRequest;
import com.restro.dto.request.UpdateCartItemRequest;
import com.restro.dto.response.CartResponse;
import com.restro.entity.*;
import com.restro.mapper.CartMapper;
import com.restro.repository.*;
import com.restro.service.CartService;
import com.restro.entity.MenuItem;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final CartMapper cartMapper;
    private final JwtUtil jwtUtil;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, UserRepository userRepository, MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository, CartMapper cartMapper, JwtUtil jwtUtil) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.cartMapper = cartMapper;
        this.jwtUtil = jwtUtil;
    }

    // 🔥 Get current user from JWT (SecurityContext)
    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 🔥 Get ACTIVE cart
    private Cart getActiveCartEntity(User user) {
        return cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    @Override
    public CartResponse addToCart(AddToCartRequest request) {

        // Step 1: Get user from SecurityContext (set by JwtFilter)
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Fetch Menu Item
        MenuItem menuItem = menuItemRepository.findById(request.getMenuId())
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        // Step 3: Fetch Restaurant
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Step 4: Get or create ACTIVE cart
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setStatus(CartStatus.ACTIVE);
                    newCart.setCartItems(new ArrayList<>());
                    newCart.setDeliveryFee(BigDecimal.valueOf(40));
                    newCart.setTaxAmount(BigDecimal.valueOf(20));
                    newCart.setDiscountAmount(BigDecimal.ZERO);
                    return cartRepository.save(newCart);
                });

        // Step 5: Business rule → one restaurant per cart
        // 🔥 IMPORTANT: attach restaurant to cart
        if (cart.getRestaurant() == null) {
            cart.setRestaurant(restaurant);
        } else if (!cart.getRestaurant().getId().equals(restaurant.getId())) {
            throw new RuntimeException(
                    "Your cart contains items from another restaurant. Clear cart to continue."
            );
        }

        // Step 6: Check existing item
        CartItem existingItem = cartItemRepository
                .findByCartAndMenuItem(cart, menuItem)
                .orElse(null);

        if (existingItem != null) {

            existingItem.setQuantity(
                    existingItem.getQuantity() + request.getQuantity()
            );

            existingItem.setTotalPrice(
                    existingItem.getPricePerUnit()
                            .multiply(BigDecimal.valueOf(existingItem.getQuantity()))
            );

            cartItemRepository.save(existingItem);

        } else {

            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setMenuItem(menuItem);
            cartItem.setQuantity(request.getQuantity());

            cartItem.setPricePerUnit(
                    BigDecimal.valueOf(menuItem.getPrice())
            );

            cartItem.setTotalPrice(
                    BigDecimal.valueOf(menuItem.getPrice())
                            .multiply(BigDecimal.valueOf(request.getQuantity()))
            );

            cartItem.setSpecialInstructions(
                    request.getSpecialInstructions()
            );

            cart.getCartItems().add(cartItem);
        }

        // Step 7: Recalculate cart totals
        recalculate(cart);

        // Step 8: Save cart
        cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }

    private void recalculate(Cart cart) {

        BigDecimal subTotal = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setSubTotal(subTotal);

        cart.setGrandTotal(
                subTotal
                        .add(cart.getDeliveryFee())
                        .add(cart.getTaxAmount())
                        .subtract(cart.getDiscountAmount())
        );
    }

    @Override
    public CartResponse getActiveCart() {

        User user = getLoggedInUser();
        Cart cart = getActiveCartEntity(user);

        return cartMapper.toCartResponse(cart);
    }

    // =========================
    // 2. UPDATE ITEM QTY
    // =========================
    @Override
    public CartResponse updateItemQuantity(UpdateCartItemRequest request) {

        CartItem item = cartItemRepository.findById(request.getCartItemId())
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(request.getQuantity());

        item.setTotalPrice(
                item.getPricePerUnit()
                        .multiply(BigDecimal.valueOf(request.getQuantity()))
        );

        cartItemRepository.save(item);

        recalculate(item.getCart());

        return cartMapper.toCartResponse(item.getCart());
    }

    // =========================
    // 3. REMOVE ITEM
    // =========================
    @Override
    public CartResponse removeItem(Long cartItemId) {

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Cart cart = item.getCart();

        cartItemRepository.delete(item);

        recalculate(cart);

        return cartMapper.toCartResponse(cart);
    }

    // =========================
    // 4. CLEAR CART
    // =========================
    @Override
    public String clearCart() {

        User user = getLoggedInUser();
        Cart cart = getActiveCartEntity(user);

        cartItemRepository.deleteAll(cart.getCartItems());

        cart.setSubTotal(BigDecimal.ZERO);
        cart.setGrandTotal(BigDecimal.ZERO);

        cartRepository.save(cart);

        return "Cart cleared successfully";
    }


}
