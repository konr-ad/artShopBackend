package com.artshop.backend.api.dto.discount;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiscountCodeCreateRequest(
        String code,
        String discountType,          // "PERCENTAGE" | "FIXED"
        BigDecimal discountValue,
        BigDecimal minimumOrderValue,
        boolean isActive,
        int usageLimit,
        LocalDate validFrom,
        LocalDate validTo
) {}