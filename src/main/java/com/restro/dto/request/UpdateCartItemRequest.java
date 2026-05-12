package com.restro.dto.request;


import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCartItemRequest {

    private UUID cartItemId;
    private Integer quantity;


    public UUID getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(UUID cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
