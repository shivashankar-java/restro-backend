package com.restro.repository;

import com.restro.entity.MenuItem;
import com.restro.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByMenuItemsContaining(MenuItem menuItem);
}
