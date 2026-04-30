package com.restro.mapper;

import com.restro.dto.response.CouponResponse;
import com.restro.entity.Coupon;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    CouponResponse toCouponResponse(Coupon coupon);
}
