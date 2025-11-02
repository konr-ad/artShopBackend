package com.artshop.backend.api.dto.discount;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DiscountCodeCreateRequest(
        String code,
        String discountType,          // "PERCENTAGE" | "FIXED"
        BigDecimal discountValue,
        BigDecimal minimumOrderValue,
        boolean isActive,
        LocalDateTime validFrom,
        LocalDateTime validTo
) {}