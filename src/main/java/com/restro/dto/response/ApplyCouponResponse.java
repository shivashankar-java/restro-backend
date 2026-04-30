package com.restro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplyCouponResponse {

    private String message;
    private BigDecimal discountAmount;
    private BigDecimal grandTotal;

    public ApplyCouponResponse(String message, BigDecimal discountAmount, BigDecimal grandTotal) {
        this.message = message;
        this.discountAmount = discountAmount;
        this.grandTotal = grandTotal;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }
}
