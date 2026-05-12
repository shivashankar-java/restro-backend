package com.restro.dto.request;

import java.util.UUID;

public class ApplyCouponRequest {

    private UUID cartId;
    private String couponCode;

    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
