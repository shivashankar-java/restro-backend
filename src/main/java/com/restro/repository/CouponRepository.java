package com.restro.repository;

import com.restro.entity.Coupon;
import com.restro.entity.CouponStatus;
import com.restro.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    List<Coupon> findByRestaurant(Restaurant restaurant);

    List<Coupon> findByRestaurantAndStatus(
            Restaurant restaurant,
            CouponStatus status
    );

    Optional<Coupon> findByCouponCode(String couponCode);

}
