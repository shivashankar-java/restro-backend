package com.restro.controller;


import com.restro.dto.request.ApplyCouponRequest;
import com.restro.dto.request.CreateCouponRequest;
import com.restro.dto.response.ApplyCouponResponse;
import com.restro.dto.response.CouponResponse;
import com.restro.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(
            @RequestBody CreateCouponRequest request) {
        return ResponseEntity.ok(couponService.createCoupon(request));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/apply")
    public ResponseEntity<ApplyCouponResponse> applyCoupon(
            @RequestBody ApplyCouponRequest request) {
        return ResponseEntity.ok(couponService.applyCoupon(request));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/remove/{cartId}")
    public ResponseEntity<ApplyCouponResponse> removeCoupon(
            @PathVariable Long cartId) {
        return ResponseEntity.ok(couponService.removeCoupon(cartId));
    }

    @GetMapping("/{code}")
    public ResponseEntity<CouponResponse> getCouponByCode(
            @PathVariable String code) {
        return ResponseEntity.ok(couponService.getCouponByCode(code));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/disable/{couponId}")
    public ResponseEntity<String> disableCoupon(
            @PathVariable Long couponId) {
        return ResponseEntity.ok(couponService.disableCoupon(couponId));
    }
}
