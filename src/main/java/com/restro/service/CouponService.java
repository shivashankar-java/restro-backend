package com.restro.service;

import com.restro.dto.request.ApplyCouponRequest;
import com.restro.dto.request.CreateCouponRequest;
import com.restro.dto.response.ApplyCouponResponse;
import com.restro.dto.response.CouponResponse;

import java.util.List;

public interface CouponService {

    CouponResponse createCoupon(CreateCouponRequest request);

    List<CouponResponse> getAllCoupons();

    ApplyCouponResponse applyCoupon(ApplyCouponRequest request);

    ApplyCouponResponse removeCoupon(Long cartId);

    CouponResponse getCouponByCode(String code);

    String disableCoupon(Long couponId);
}
