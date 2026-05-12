package com.restro.repository;

import com.restro.entity.Cart;
import com.restro.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import com.restro.entity.MenuItem;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCartAndMenuItem(
            Cart cart,
            MenuItem menuItem
    );
}
