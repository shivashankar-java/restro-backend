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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger =
            LogManager.getLogger(CartServiceImpl.class);

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

    // =========================
    // GET LOGGED-IN USER
    // =========================
    private User getLoggedInUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        logger.info("Fetching logged-in user with email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new RuntimeException("User not found");
                });
    }

    // =========================
    // GET ACTIVE CART
    // =========================
    private Cart getActiveCartEntity(User user) {

        return cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> {
                    logger.error("Active cart not found for user: {}", user.getEmail());
                    return new RuntimeException("Active cart not found");
                });
    }

    // ADD TO CART
    @Override
    public CartResponse addToCart(AddToCartRequest request) {

        logger.info("Add to cart request started for menuId: {}", request.getMenuId());

        User user = getLoggedInUser();

        MenuItem menuItem = menuItemRepository.findById(request.getMenuId())
                .orElseThrow(() -> {
                    logger.error("Menu item not found with id: {}", request.getMenuId());
                    return new RuntimeException("Menu item not found");
                });

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> {
                    logger.error("Restaurant not found with id: {}", request.getRestaurantId());
                    return new RuntimeException("Restaurant not found");
                });

        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    logger.info("Creating new cart for user: {}", user.getEmail());

                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setRestaurant(restaurant);
                    newCart.setStatus(CartStatus.ACTIVE);
                    newCart.setCartItems(new ArrayList<>());
                    newCart.setDeliveryFee(BigDecimal.valueOf(40));
                    newCart.setTaxAmount(BigDecimal.valueOf(20));
                    newCart.setDiscountAmount(BigDecimal.ZERO);
                    newCart.setSubTotal(BigDecimal.ZERO);
                    newCart.setGrandTotal(BigDecimal.ZERO);

                    return cartRepository.save(newCart);
                });

        // One restaurant per cart rule
        if (cart.getRestaurant() != null &&
                !cart.getRestaurant().getId().equals(restaurant.getId())) {

            logger.warn("Cart contains items from another restaurant");

            throw new RuntimeException(
                    "Your cart contains items from another restaurant. Clear cart to continue."
            );
        }

        CartItem existingItem = cartItemRepository
                .findByCartAndMenuItem(cart, menuItem)
                .orElse(null);

        if (existingItem != null) {

            logger.info("Updating existing cart item");

            existingItem.setQuantity(
                    existingItem.getQuantity() + request.getQuantity()
            );

            existingItem.setTotalPrice(
                    existingItem.getPricePerUnit()
                            .multiply(BigDecimal.valueOf(existingItem.getQuantity()))
            );

            cartItemRepository.save(existingItem);

        } else {

            logger.info("Adding new item to cart");

            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setMenuItem(menuItem);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPricePerUnit(BigDecimal.valueOf(menuItem.getPrice()));

            cartItem.setTotalPrice(
                    BigDecimal.valueOf(menuItem.getPrice())
                            .multiply(BigDecimal.valueOf(request.getQuantity()))
            );

            cartItem.setSpecialInstructions(
                    request.getSpecialInstructions()
            );

            cartItemRepository.save(cartItem);

            cart.getCartItems().add(cartItem);
        }

        recalculate(cart);
        cartRepository.save(cart);

        logger.info("Item added to cart successfully");

        return cartMapper.toCartResponse(cart);
    }

    // RECALCULATE TOTALS
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

        logger.info("Cart totals recalculated successfully");
    }



    // =========================
    // GET ACTIVE CART
    // =========================
    @Override
    public CartResponse getActiveCart() {

        logger.info("Fetching active cart");

        User user = getLoggedInUser();
        Cart cart = getActiveCartEntity(user);

        return cartMapper.toCartResponse(cart);
    }

    // UPDATE ITEM QUANTITY
    @Override
    public CartResponse updateItemQuantity(UpdateCartItemRequest request) {

        logger.info("Updating cart item quantity for cartItemId: {}",
                request.getCartItemId());

        CartItem item = cartItemRepository.findById(request.getCartItemId())
                .orElseThrow(() -> {
                    logger.error("Cart item not found");
                    return new RuntimeException("Cart item not found");
                });

        item.setQuantity(request.getQuantity());

        item.setTotalPrice(
                item.getPricePerUnit()
                        .multiply(BigDecimal.valueOf(request.getQuantity()))
        );

        cartItemRepository.save(item);

        Cart cart = item.getCart();
        recalculate(cart);
        cartRepository.save(cart);

        logger.info("Cart item quantity updated successfully");

        return cartMapper.toCartResponse(cart);
    }

    // REMOVE ITEM
    @Override
    public CartResponse removeItem(Long cartItemId) {

        logger.info("Removing cart item with id: {}", cartItemId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> {
                    logger.error("Cart item not found");
                    return new RuntimeException("Cart item not found");
                });

        Cart cart = item.getCart();

        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);

        recalculate(cart);
        cartRepository.save(cart);

        logger.info("Cart item removed successfully");

        return cartMapper.toCartResponse(cart);
    }


    // CLEAR CART
    @Override
    public String clearCart() {

        logger.info("Clear cart request started");

        User user = getLoggedInUser();
        Cart cart = getActiveCartEntity(user);

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();

        cart.setSubTotal(BigDecimal.ZERO);
        cart.setGrandTotal(BigDecimal.ZERO);

        cartRepository.save(cart);

        logger.info("Cart cleared successfully");

        return "Cart cleared successfully";
    }

}
