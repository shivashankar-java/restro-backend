package com.restro.controller;

import com.restro.dto.request.RestaurantRequest;
import com.restro.dto.response.RestaurantResponse;
import com.restro.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // ADMIN ONLY → Add Restaurant
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RestaurantResponse> addRestaurant(
            @RequestBody RestaurantRequest request) {

        return ResponseEntity.ok(
                restaurantService.addRestaurant(request)
        );
    }

    // CUSTOMER + ADMIN → Get restaurants by menu item
    @GetMapping("/by-menu/{menuId}")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByMenu(
            @PathVariable Long menuId) {

        return ResponseEntity.ok(
                restaurantService.getRestaurantsByMenu(menuId)
        );
    }

    // Get all restaurants
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        return ResponseEntity.ok(
                restaurantService.getAllRestaurants()
        );
    }



}
