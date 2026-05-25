package com.restro.service.impl;

import com.restro.dto.request.ApplyCouponRequest;
import com.restro.dto.request.CreateCouponRequest;
import com.restro.dto.response.ApplyCouponResponse;
import com.restro.dto.response.CouponResponse;
import com.restro.entity.*;
import com.restro.mapper.CouponMapper;
import com.restro.repository.CartRepository;
import com.restro.repository.CouponRepository;
import com.restro.repository.UserRepository;
import com.restro.service.CouponService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CouponMapper couponMapper;

    public CouponServiceImpl(CouponRepository couponRepository, UserRepository userRepository, CouponMapper couponMapper) {
        this.couponRepository = couponRepository;
        this.userRepository = userRepository;
        this.couponMapper = couponMapper;
    }

    // GET LOGGED-IN RESTAURANT
    private Restaurant getLoggedInRestaurant() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getRestaurant();
    }

    @Override
    public CouponResponse createCoupon(CreateCouponRequest request) {

        Restaurant restaurant = getLoggedInRestaurant();
        Coupon coupon = couponMapper.toEntity(request);
        coupon.setRestaurant(restaurant);
        coupon.setUsedCount(0);
        coupon.setStatus(CouponStatus.ACTIVE);
        Coupon savedCoupon = couponRepository.save(coupon);
        return couponMapper.toResponse(savedCoupon);
    }

    @Override
    public CouponResponse updateCoupon(UUID couponId, CreateCouponRequest request) {

        Restaurant restaurant = getLoggedInRestaurant();
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        if (!coupon.getRestaurant().getId()
                .equals(restaurant.getId())) {

            throw new RuntimeException("Unauthorized access");
        }

        couponMapper.updateCoupon(request, coupon);
        Coupon updatedCoupon = couponRepository.save(coupon);
        return couponMapper.toResponse(updatedCoupon);
    }

    @Override
    public CouponResponse getCouponById(UUID couponId) {

        Restaurant restaurant = getLoggedInRestaurant();

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        if (!coupon.getRestaurant().getId()
                .equals(restaurant.getId())) {

            throw new RuntimeException("Unauthorized access");
        }

        return couponMapper.toResponse(coupon);
    }

    @Override
    public List<CouponResponse> getAllCoupons() {

        Restaurant restaurant = getLoggedInRestaurant();
        List<Coupon> coupons = couponRepository.findByRestaurant(restaurant);

        return coupons.stream()
                .map(couponMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteCoupon(UUID couponId) {

        Restaurant restaurant = getLoggedInRestaurant();

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        if (!coupon.getRestaurant().getId()
                .equals(restaurant.getId())) {

            throw new RuntimeException("Unauthorized access");
        }

        couponRepository.delete(coupon);
    }

    @Override
    public CouponResponse changeStatus(UUID couponId, String status) {

        Restaurant restaurant = getLoggedInRestaurant();
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        if (!coupon.getRestaurant().getId()
                .equals(restaurant.getId())) {

            throw new RuntimeException("Unauthorized access");
        }

        coupon.setStatus(CouponStatus.valueOf(status.toUpperCase()));

        // AUTO EXPIRE
        if (coupon.getEndDate() != null &&
                coupon.getEndDate().isBefore(LocalDate.now())) {
            coupon.setStatus(CouponStatus.EXPIRED);
        }

        Coupon updatedCoupon = couponRepository.save(coupon);
        return couponMapper.toResponse(updatedCoupon);
    }

    @Override
    public ApplyCouponResponse applyCoupon(ApplyCouponRequest request) {

        Coupon coupon = couponRepository.findByCouponCode(request.getCouponCode())
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

        // CHECK STATUS
        if (coupon.getStatus() != CouponStatus.ACTIVE) {
            throw new RuntimeException("Coupon is not active");
        }

        // CHECK EXPIRY
        if (coupon.getEndDate() != null &&
                coupon.getEndDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Coupon expired");
        }

        // CHECK MIN ORDER AMOUNT
        if (request.getOrderAmount() < coupon.getMinimumOrderAmount()) {
            throw new RuntimeException("Minimum order amount should be "
                    + coupon.getMinimumOrderAmount());
        }

        // CHECK USAGE LIMIT
        if (coupon.getUsageLimit() != null &&
                coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new RuntimeException("Coupon usage limit exceeded");
        }

        double discountAmount = 0;

        // PERCENTAGE DISCOUNT
        if (coupon.getDiscountType().name().equals("PERCENTAGE")) {
            discountAmount = (request.getOrderAmount() * coupon.getDiscountValue()) / 100;
        }

        // FLAT DISCOUNT
        else if (coupon.getDiscountType().name().equals("FLAT")) {
            discountAmount = coupon.getDiscountValue();
        }

        double finalAmount = request.getOrderAmount() - discountAmount;

        if (finalAmount < 0) {
            finalAmount = 0;
        }

        // INCREMENT USED COUNT
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);

        return new ApplyCouponResponse(coupon.getCouponCode(), request.getOrderAmount(),
                discountAmount, finalAmount,
                "Coupon applied successfully");
    }


}
