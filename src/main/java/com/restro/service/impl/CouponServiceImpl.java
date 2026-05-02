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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    private static final Logger logger =
            LogManager.getLogger(CouponServiceImpl.class);

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

        logger.info("Create coupon request received for code: {}", request.getCode());

        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode());
        coupon.setDescription(request.getDescription());

        coupon.setDiscountType(
                DiscountType.valueOf(request.getDiscountType())
        );

        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinimumOrderAmount(request.getMinimumOrderAmount());
        coupon.setMaximumDiscount(request.getMaximumDiscount());
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidUntil(request.getValidUntil());
        coupon.setActive(request.getActive());

        coupon = couponRepository.save(coupon);

        logger.info("Coupon created successfully: {}", coupon.getCode());

        return couponMapper.toCouponResponse(coupon);
    }

    @Override
    public List<CouponResponse> getAllCoupons() {

        logger.info("Fetching all active coupons");

        List<CouponResponse> coupons = couponRepository.findByActiveTrue()
                .stream()
                .map(couponMapper::toCouponResponse)
                .toList();

        logger.info("Total active coupons found: {}", coupons.size());

        return coupons;
    }

    @Override
    public ApplyCouponResponse applyCoupon(ApplyCouponRequest request) {

        logger.info("Apply coupon request received for cartId: {}, couponCode: {}",
                request.getCartId(),
                request.getCouponCode());

        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> {
                    logger.error("Cart not found for ID: {}", request.getCartId());
                    return new RuntimeException("Cart not found");
                });

        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                .orElseThrow(() -> {
                    logger.error("Coupon not found: {}", request.getCouponCode());
                    return new RuntimeException("Coupon not found");
                });

        // Check active
        if (Boolean.FALSE.equals(coupon.getActive())) {
            logger.warn("Coupon is inactive: {}", coupon.getCode());
            throw new RuntimeException("Coupon inactive");
        }

        // Check validity
        LocalDateTime now = LocalDateTime.now();

        if (coupon.getValidFrom() != null &&
                now.isBefore(coupon.getValidFrom())) {

            logger.warn("Coupon not yet active: {}", coupon.getCode());
            throw new RuntimeException("Coupon not yet active");
        }

        if (coupon.getValidUntil() != null &&
                now.isAfter(coupon.getValidUntil())) {

            logger.warn("Coupon expired: {}", coupon.getCode());
            throw new RuntimeException("Coupon expired");
        }

        // Minimum order check
        if (cart.getSubTotal().compareTo(
                coupon.getMinimumOrderAmount()) < 0) {

            logger.warn("Minimum order amount not reached for coupon: {}",
                    coupon.getCode());

            throw new RuntimeException("Minimum order amount not reached");
        }

        // Calculate discount
        BigDecimal discount = BigDecimal.ZERO;

        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {

            discount = cart.getSubTotal()
                    .multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));

        } else if (coupon.getDiscountType() == DiscountType.FIXED) {

            discount = coupon.getDiscountValue();
        }

        // Maximum discount cap
        if (coupon.getMaximumDiscount() != null &&
                discount.compareTo(coupon.getMaximumDiscount()) > 0) {

            discount = coupon.getMaximumDiscount();
        }

        // Update cart
        cart.setDiscountAmount(discount);

        cart.setGrandTotal(
                cart.getSubTotal()
                        .add(cart.getDeliveryFee())
                        .add(cart.getTaxAmount())
                        .subtract(discount)
        );

        cartRepository.save(cart);

        logger.info("Coupon applied successfully. Discount: {}, Grand Total: {}",
                discount,
                cart.getGrandTotal());

        return new ApplyCouponResponse(
                "Coupon applied successfully",
                discount,
                cart.getGrandTotal()
        );
    }

    @Override
    public ApplyCouponResponse removeCoupon(Long cartId) {

        logger.info("Remove coupon request received for cartId: {}", cartId);

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> {
                    logger.error("Cart not found for ID: {}", cartId);
                    return new RuntimeException("Cart not found");
                });

        cart.setDiscountAmount(BigDecimal.ZERO);

        cart.setGrandTotal(
                cart.getSubTotal()
                        .add(cart.getDeliveryFee())
                        .add(cart.getTaxAmount())
        );

        cartRepository.save(cart);

        logger.info("Coupon removed successfully for cartId: {}", cartId);

        return new ApplyCouponResponse(
                "Coupon removed successfully",
                BigDecimal.ZERO,
                cart.getGrandTotal()
        );
    }

    @Override
    public CouponResponse getCouponByCode(String code) {

        logger.info("Fetching coupon by code: {}", code);

        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> {
                    logger.error("Coupon not found: {}", code);
                    return new RuntimeException("Coupon not found");
                });

        logger.info("Coupon found successfully: {}", code);

        return couponMapper.toCouponResponse(coupon);
    }

    @Override
    public String disableCoupon(Long couponId) {

        logger.info("Disable coupon request received for couponId: {}", couponId);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> {
                    logger.error("Coupon not found for ID: {}", couponId);
                    return new RuntimeException("Coupon not found");
                });

        coupon.setActive(false);
        couponRepository.save(coupon);

        logger.info("Coupon disabled successfully: {}", coupon.getCode());

        return "Coupon disabled successfully";
    }
}
