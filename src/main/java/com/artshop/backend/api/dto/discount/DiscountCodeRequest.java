package com.artshop.backend.api.dto.discount;

import java.math.BigDecimal;

public record DiscountCodeRequest(
        String code,
        BigDecimal orderValue
) {}