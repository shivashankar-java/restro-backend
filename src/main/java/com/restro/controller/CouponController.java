package com.restro.controller;

import com.restro.dto.request.ApplyCouponRequest;
import com.restro.dto.request.CreateCouponRequest;
import com.restro.dto.response.ApplyCouponResponse;
import com.restro.dto.response.CouponResponse;
import com.restro.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurant/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // CREATE COUPON
    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(
            @RequestBody CreateCouponRequest request) {

        CouponResponse response = couponService.createCoupon(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // UPDATE COUPON
    @PutMapping("/{couponId}")
    public ResponseEntity<CouponResponse> updateCoupon(@PathVariable UUID couponId,
            @RequestBody CreateCouponRequest request) {

        CouponResponse response = couponService.updateCoupon(couponId, request);
        return ResponseEntity.ok(response);
    }

    // GET COUPON BY ID
    @GetMapping("/{couponId}")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable UUID couponId) {

        CouponResponse response = couponService.getCouponById(couponId);
        return ResponseEntity.ok(response);
    }

    // GET ALL COUPONS
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {

        List<CouponResponse> response = couponService.getAllCoupons();
        return ResponseEntity.ok(response);
    }

    // DELETE COUPON
    @DeleteMapping("/{couponId}")
    public ResponseEntity<String> deleteCoupon(@PathVariable UUID couponId) {

        couponService.deleteCoupon(couponId);
        return ResponseEntity.ok("Coupon deleted successfully");
    }

    // CHANGE STATUS
    @PatchMapping("/{couponId}/status")
    public ResponseEntity<CouponResponse> changeStatus(@PathVariable UUID couponId,
            @RequestParam String status) {

        CouponResponse response = couponService.changeStatus(couponId, status);
        return ResponseEntity.ok(response);
    }

    // APPLY COUPON
    @PostMapping("/apply")
    public ResponseEntity<ApplyCouponResponse> applyCoupon(
            @RequestBody ApplyCouponRequest request) {

        ApplyCouponResponse response = couponService.applyCoupon(request);
        return ResponseEntity.ok(response);
    }

}
