package com.restro.dto.request;

import java.util.UUID;

public class PlaceOrderRequest {

    private UUID cartId;
    private String paymentMethod; // COD / UPI / CARD

    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
