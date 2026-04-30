package com.restro.repository;


import com.restro.entity.Cart;
import com.restro.entity.CartStatus;
import com.restro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserAndStatus(User user, CartStatus status);

    Optional<Cart> findByUser(User user);
}