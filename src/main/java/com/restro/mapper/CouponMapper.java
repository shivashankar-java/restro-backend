package com.restro.mapper;

import com.restro.dto.request.CreateCouponRequest;
import com.restro.dto.response.CouponResponse;
import com.restro.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    Coupon toEntity(CreateCouponRequest request);

    CouponResponse toResponse(Coupon coupon);

    void updateCoupon(CreateCouponRequest request,
                      @MappingTarget Coupon coupon);
}
