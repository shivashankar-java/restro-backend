package com.restro.service.impl;

import com.restro.config.JwtUtil;
import com.restro.dto.request.AddToCartRequest;
import com.restro.dto.response.CartResponse;
import com.restro.entity.*;
import com.restro.mapper.CartMapper;
import com.restro.repository.*;
import com.restro.service.CartService;
import com.restro.entity.MenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

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

    private User getLoggedInUser(){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public CartResponse addItem(AddToCartRequest request){

        User user = getLoggedInUser();

        MenuItem menu = menuItemRepository.findById(request.getMenuId())
                        .orElseThrow(() -> new RuntimeException("Menu not found"));

        // find user active cart
        Cart cart = cartRepository.findByUserIdAndRestaurantIdAndStatus(
                                user.getId(),
                                menu.getRestaurant().getId(),
                                CartStatus.ACTIVE)
                        .orElse(null);

        // create cart

        if(cart == null){
            cart = new Cart();
            cart.setUser(user);
            cart.setRestaurant(menu.getRestaurant());
            cart.setStatus(CartStatus.ACTIVE);

            cart.setSubTotal(BigDecimal.ZERO);
            cart.setTaxAmount(BigDecimal.ZERO);
            cart.setDeliveryFee(BigDecimal.ZERO);
            cart.setGrandTotal(BigDecimal.ZERO);

            cart = cartRepository.save(cart);
        }

        // find item

        CartItem item = cartItemRepository
                        .findByCartIdAndMenuItemId(
                                cart.getId(),
                                menu.getId())
                        .orElse(null);

        if(item == null){

            item = new CartItem();

            item.setCart(cart);

            item.setMenuItem(menu);

            item.setQuantity(
                    request.getQuantity()
            );

        }else{

            item.setQuantity(
                    item.getQuantity()
                            +
                            request.getQuantity()
            );
        }

        item.setPricePerUnit(
                BigDecimal.valueOf(
                        menu.getPrice()
                )
        );

        item.setTotalPrice(
                item.getPricePerUnit()
                        .multiply(
                                BigDecimal.valueOf(
                                        item.getQuantity()
                                )
                        )
        );

        cartItemRepository.save(item);
        calculateCart(cart);
        cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    private void calculateCart(Cart cart){

        BigDecimal subtotal = cartItemRepository
                        .findByCartId(cart.getId())
                        .stream()
                        .map(CartItem::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setSubTotal(subtotal);
        cart.setTaxAmount(subtotal.multiply(BigDecimal.valueOf(0.05)));
        cart.setDeliveryFee(BigDecimal.valueOf(40));
        cart.setGrandTotal(subtotal.add(cart.getTaxAmount())
                        .add(cart.getDeliveryFee()));
    }


    @Override
    public CartResponse getCart(UUID cartId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return cartMapper.toResponse(cart);
    }

    @Override
    public CartResponse updateItem(UUID cartItemId, Integer quantity) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if(quantity <= 0){
            cartItemRepository.delete(cartItem);
            Cart cart = cartItem.getCart();
            calculateCart(cart);
            cartRepository.save(cart);

            return cartMapper.toResponse(cart);
        }

        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(cartItem.getPricePerUnit()
                        .multiply(BigDecimal.valueOf(quantity)));

        cartItemRepository.save(cartItem);

        Cart cart = cartItem.getCart();
        calculateCart(cart);
        cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    @Override
    public void removeItem(UUID cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);
        calculateCart(cart);
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(UUID cartId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteAll(cart.getCartItems());

        cart.setSubTotal(BigDecimal.ZERO);
        cart.setTaxAmount(BigDecimal.ZERO);
        cart.setDeliveryFee(BigDecimal.ZERO);
        cart.setDiscountAmount(BigDecimal.ZERO);
        cart.setGrandTotal(BigDecimal.ZERO);
        cart.setRestaurant(null);

        cartRepository.save(cart);
    }

}
