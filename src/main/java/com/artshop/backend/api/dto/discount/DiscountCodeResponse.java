package com.artshop.backend.api.dto.discount;

import java.math.BigDecimal;

public record DiscountCodeResponse(
        boolean valid,
        String message,
        BigDecimal discountValue
) {
    public static DiscountCodeResponse invalid(String msg) {
        return new DiscountCodeResponse(false, msg, BigDecimal.ZERO);
    }
}