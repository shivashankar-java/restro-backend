package com.restro.dto.request;

import java.util.UUID;

public class ApplyCouponRequest {

    private String couponCode;
    private Double orderAmount;

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }
}
