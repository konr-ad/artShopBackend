package com.artShop.artShop.models.discountCodeDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountCodeRequest {

    private String code;
    private BigDecimal orderValue;
}
