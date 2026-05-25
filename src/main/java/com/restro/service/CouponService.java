package com.restro.service;

import com.restro.dto.request.ApplyCouponRequest;
import com.restro.dto.request.CreateCouponRequest;
import com.restro.dto.response.ApplyCouponResponse;
import com.restro.dto.response.CouponResponse;

import java.util.List;
import java.util.UUID;

public interface CouponService {

    CouponResponse createCoupon(CreateCouponRequest request);
    CouponResponse updateCoupon(UUID couponId, CreateCouponRequest request);
    CouponResponse getCouponById(UUID couponId);
    List<CouponResponse> getAllCoupons();
    void deleteCoupon(UUID couponId);
    CouponResponse changeStatus(UUID couponId, String status);

    ApplyCouponResponse applyCoupon(ApplyCouponRequest request);
}
