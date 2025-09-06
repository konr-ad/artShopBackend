package com.artShop.models.entity;

import com.artShop.enums.EDiscountType;
import com.artShop.models.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "discount_code", indexes = {
        @Index(name="ix_discount_active_to", columnList="is_active, valid_to")
})
@Getter
@Setter
@NoArgsConstructor
public class DiscountCode extends BaseEntity {
    @Column(nullable=false, unique=true, length=64)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=32)
    private EDiscountType discountType;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal discountValue;

    @Column(precision=12, scale=2)
    private BigDecimal minimumOrderValue;

    @Column(nullable=false)
    private int usageLimit;

    @Column(nullable=false)
    private int timesUsed;

    @Column(name = "is_active", nullable=false)
    private boolean active;

    private LocalDate validFrom;
    private LocalDate validTo;
}
