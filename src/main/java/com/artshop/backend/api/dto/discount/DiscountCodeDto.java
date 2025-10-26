package com.artshop.backend.api.dto.discount;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiscountCodeDto(
        Long id,
        String code,
        String discountType,
        BigDecimal discountValue,
        BigDecimal minimumOrderValue,
        boolean isActive,
        int usageLimit,
        int timesUsed,
        LocalDate validFrom,
        LocalDate validTo
) {}