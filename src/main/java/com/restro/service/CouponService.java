package com.restro.service;

import com.restro.dto.request.ApplyCouponRequest;
import com.restro.dto.request.CreateCouponRequest;
import com.restro.dto.response.ApplyCouponResponse;
import com.restro.dto.response.CouponResponse;

import java.util.List;
import java.util.UUID;

public interface CouponService {

    CouponResponse createCoupon(CreateCouponRequest request);

    List<CouponResponse> getAllCoupons();

    ApplyCouponResponse applyCoupon(ApplyCouponRequest request);

    ApplyCouponResponse removeCoupon(UUID cartId);

    CouponResponse getCouponByCode(String code);

    String disableCoupon(UUID couponId);
}
