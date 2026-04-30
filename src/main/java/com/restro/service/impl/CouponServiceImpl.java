package com.restro.service.impl;

import com.restro.dto.request.ApplyCouponRequest;
import com.restro.dto.request.CreateCouponRequest;
import com.restro.dto.response.ApplyCouponResponse;
import com.restro.dto.response.CouponResponse;
import com.restro.entity.Cart;
import com.restro.entity.Coupon;
import com.restro.entity.DiscountType;
import com.restro.mapper.CouponMapper;
import com.restro.repository.CartRepository;
import com.restro.repository.CouponRepository;
import com.restro.service.CouponService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CartRepository cartRepository;
    private final CouponMapper couponMapper;

    public CouponServiceImpl(CouponRepository couponRepository, CartRepository cartRepository, CouponMapper couponMapper) {
        this.couponRepository = couponRepository;
        this.cartRepository = cartRepository;
        this.couponMapper = couponMapper;
    }

    @Override
    public CouponResponse createCoupon(CreateCouponRequest request) {

        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode());
        coupon.setDescription(request.getDescription());

        coupon.setDiscountType(DiscountType.valueOf(request.getDiscountType()));

        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinimumOrderAmount(request.getMinimumOrderAmount());

        coupon.setMaximumDiscount(request.getMaximumDiscount());

        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidUntil(request.getValidUntil());

        coupon.setActive(request.getActive());
        coupon = couponRepository.save(coupon);
        return couponMapper.toCouponResponse(coupon);
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findByActiveTrue()
                .stream()
                .map(couponMapper::toCouponResponse)
                .toList();
    }

    @Override
    public ApplyCouponResponse applyCoupon(ApplyCouponRequest request) {

        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        // 1. Check active
        if (Boolean.FALSE.equals(coupon.getActive())) {
            throw new RuntimeException("Coupon inactive");
        }
        // 2. Check validity window
        LocalDateTime now = LocalDateTime.now();

        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            throw new RuntimeException("Coupon not yet active");
        }

        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            throw new RuntimeException("Coupon expired");
        }
        // 3. Minimum order check
        if (cart.getSubTotal().compareTo(coupon.getMinimumOrderAmount()) < 0) {
            throw new RuntimeException("Minimum order amount not reached");
        }

        // 4. Calculate discount properly
        BigDecimal discount = BigDecimal.ZERO;

        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {

            discount = cart.getSubTotal()
                    .multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));

        } else if (coupon.getDiscountType() == DiscountType.FIXED) {
            discount = coupon.getDiscountValue();
        }

        // 5. Apply max discount cap
        if (coupon.getMaximumDiscount() != null &&
                discount.compareTo(coupon.getMaximumDiscount()) > 0) {
            discount = coupon.getMaximumDiscount();
        }

        // 6. Update cart
        cart.setDiscountAmount(discount);

        cart.setGrandTotal(
                cart.getSubTotal()
                        .add(cart.getDeliveryFee())
                        .add(cart.getTaxAmount())
                        .subtract(discount)
        );

        cartRepository.save(cart);

        // 7. Response
        return new ApplyCouponResponse(
                "Coupon applied successfully",
                discount,
                cart.getGrandTotal()
        );
    }


    @Override
    public ApplyCouponResponse removeCoupon(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.setDiscountAmount(BigDecimal.ZERO);
        cart.setGrandTotal(
                cart.getSubTotal()
                        .add(cart.getDeliveryFee())
                        .add(cart.getTaxAmount())
        );

        cartRepository.save(cart);

        return new ApplyCouponResponse(
                "Coupon removed successfully",
                BigDecimal.ZERO,
                cart.getGrandTotal()
        );
    }

    @Override
    public CouponResponse getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        return couponMapper.toCouponResponse(coupon);
    }

    @Override
    public String disableCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        coupon.setActive(false);
        couponRepository.save(coupon);

        return "Coupon disabled successfully";
    }
}
