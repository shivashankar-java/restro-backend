package com.restro.service.impl;

import com.restro.dto.request.AddToCartRequest;
import com.restro.dto.request.UpdateCartItemRequest;
import com.restro.dto.response.CartResponse;
import com.restro.entity.*;
import com.restro.mapper.CartMapper;
import com.restro.repository.*;
import com.restro.service.CartService;
import com.restro.entity.MenuItem;
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

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, UserRepository userRepository, MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    public CartResponse addToCart(AddToCartRequest request) {

        // Fetch User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch Menu Item
        MenuItem menuItem = menuItemRepository.findById(request.getMenuId())
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        // Fetch Restaurant
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Get active cart or create new cart
        Cart cart = cartRepository
                .findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElse(null);

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setRestaurant(restaurant);
            cart.setCartItems(new ArrayList<>());
            cart.setDeliveryFee(BigDecimal.valueOf(40));
            cart.setTaxAmount(BigDecimal.valueOf(20));
            cart.setDiscountAmount(BigDecimal.ZERO);
            cart.setDeliveryAddress(request.getDeliveryAddress());
            cart.setStatus(CartStatus.ACTIVE);

            // IMPORTANT → Save first
            cart = cartRepository.save(cart);
        }

        // Restrict one restaurant per cart
        if (cart.getRestaurant() != null &&
                !cart.getRestaurant().getId().equals(restaurant.getId())) {
            throw new RuntimeException(
                    "You can only order from one restaurant at a time"
            );
        }

        // Check if same item already exists
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

            // Create new cart item
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

        // Recalculate totals
        recalculate(cart);

        // Save cart
        cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse getActiveCart(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository
                .findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        return cartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse updateCartItem(UpdateCartItemRequest request) {

        CartItem item = cartItemRepository.findById(request.getCartItemId())
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(request.getQuantity());

        item.setTotalPrice(
                item.getPricePerUnit()
                        .multiply(BigDecimal.valueOf(request.getQuantity()))
        );

        cartItemRepository.save(item);

        recalculate(item.getCart());
        cartRepository.save(item.getCart());

        return cartMapper.toCartResponse(item.getCart());
    }

    @Override
    public String removeCartItem(Long cartItemId) {

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Cart cart = item.getCart();

        cartItemRepository.delete(item);

        recalculate(cart);
        cartRepository.save(cart);

        return "Cart item removed successfully";
    }

    @Override
    public String clearCart(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository
                .findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteAll(cart.getCartItems());

        cart.setSubTotal(BigDecimal.ZERO);
        cart.setGrandTotal(BigDecimal.ZERO);

        cartRepository.save(cart);

        return "Cart cleared successfully";
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
}
