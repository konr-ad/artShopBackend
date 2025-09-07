package com.artshop.backend.models.payu;

import com.artshop.backend.models.base.BaseEntity;
import com.artshop.backend.enums.EPaintingType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name="order_items", indexes = {
        @Index(name="ix_order_items_order", columnList="order_id")
})
@Getter @Setter
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="order_id", nullable=false)
    private Order order;

    @Column(nullable=false)
    private Long paintingId;

    @Column(nullable=false, length=255)
    private String paintingNameSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=50)
    private EPaintingType paintingTypeSnapshot;

    @Column(nullable=false)
    private int quantity = 1;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal unitPriceAtPurchase;

    public BigDecimal lineTotal() {
        return unitPriceAtPurchase.multiply(BigDecimal.valueOf(quantity));
    }
}
