package com.restro.repository;

import com.restro.entity.Category;
import com.restro.entity.FoodType;
import com.restro.entity.MenuStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.restro.entity.MenuItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

    List<MenuItem> findByCategory_CategoryName(String categoryName);

    Page<MenuItem> findByItemNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<MenuItem> findByCategory_CategoryNameIgnoreCase(String category, Pageable pageable);

    Page<MenuItem> findByFoodType(FoodType foodType, Pageable pageable);

    Page<MenuItem> findByStatus(MenuStatus status, Pageable pageable);

    // GET ALL MENUS OF LOGGED-IN RESTAURANT
    Page<MenuItem> findByRestaurantId(UUID restaurantId, Pageable pageable);

    // SEARCH MENU ITEMS
    Page<MenuItem> findByRestaurantIdAndItemNameContainingIgnoreCase(UUID restaurantId, String keyword, Pageable pageable);

    // FILTER BY CATEGORY
    Page<MenuItem> findByRestaurantIdAndCategory_CategoryNameIgnoreCase(UUID restaurantId, String category, Pageable pageable);

    // FILTER BY FOOD TYPE
    Page<MenuItem> findByRestaurantIdAndFoodType(UUID restaurantId, FoodType foodType, Pageable pageable);

    // FILTER BY STATUS
    Page<MenuItem> findByRestaurantIdAndStatus(UUID restaurantId, MenuStatus status, Pageable pageable);

    // DASHBOARD COUNTS

    long countByRestaurantId(UUID restaurantId);

    long countByRestaurantIdAndFoodType(UUID restaurantId, FoodType foodType);

    long countByRestaurantIdAndStatus(UUID restaurantId, MenuStatus status);

    // AVERAGE PRICE
    @Query("""
            SELECT AVG(m.price)
            FROM MenuItem m
            WHERE m.restaurant.id = :restaurantId
            """)
    Double getAveragePriceByRestaurant(@Param("restaurantId") UUID restaurantId);

}
