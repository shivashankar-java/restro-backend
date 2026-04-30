package com.restro.dto.request;

public class PlaceOrderRequest {

    private Long cartId;
    private String paymentMethod; // COD / UPI / CARD

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
