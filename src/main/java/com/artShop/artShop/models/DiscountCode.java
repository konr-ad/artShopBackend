package com.artShop.artShop.models;

import com.artShop.artShop.enums.EDiscountType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "discount_code")
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String code;
    private EDiscountType discountType;
    @Column(nullable = false)
    private BigDecimal discountValue;
    private BigDecimal minimumOrderValue;
    @Column(nullable = false)
    private int usageLimit;
    private int timesUsed;
    private boolean isActive;
    private LocalDateTime validTo;
    private LocalDateTime validFrom;

}
