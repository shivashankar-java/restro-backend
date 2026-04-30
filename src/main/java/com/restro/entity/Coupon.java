package com.restro.entity;

import com.restro.entity.base.Audit;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;   //  THIS MUST EXIST

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType;

    private BigDecimal discountValue;
    private BigDecimal minimumOrderAmount;
    private BigDecimal maximumDiscount;

    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(BigDecimal minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public BigDecimal getMaximumDiscount() {
        return maximumDiscount;
    }

    public void setMaximumDiscount(BigDecimal maximumDiscount) {
        this.maximumDiscount = maximumDiscount;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
