package com.artShop.models.discountCodeDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
public class DiscountCodeRequest {

    private String code;
    private BigDecimal orderValue;
}
