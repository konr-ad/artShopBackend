package com.artShop.artShop.models.discountCodeDto;

import com.artShop.artShop.enums.EDiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
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
