package com.artshop.backend.api.dto.discount;

import com.artshop.backend.enums.EDiscountType;

import java.math.BigDecimal;

public record DiscountCodeResponse(
        boolean valid,
        String message,
        BigDecimal discountValue,
        EDiscountType discountType) {
    public static DiscountCodeResponse invalid(String msg) {
        return new DiscountCodeResponse(false, msg, BigDecimal.ZERO, EDiscountType.FIXED);
    }
}