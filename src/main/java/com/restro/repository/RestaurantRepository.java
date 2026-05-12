package com.restro.repository;

import com.restro.entity.MenuItem;
import com.restro.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    List<Restaurant> findByMenuItemsContaining(MenuItem menuItem);

    List<Restaurant> findByCategory_CategoryId(UUID categoryId);

}
