package com.artShop.models.discountCodeDto;

import com.artShop.enums.EDiscountType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
public class DiscountCodeResponse {
    private Long id;
    private String code;
    private boolean valid;
    private String message;
    private BigDecimal discountValue;
    private EDiscountType discountType;
    private BigDecimal minimumOrderValue;
    private int usageLimit;
    private int timesUsed;
    private boolean isActive;
    private LocalDateTime validTo;
    private LocalDateTime validFrom;
}
