package com.artShop.artShop.models.payu;

import com.artShop.artShop.enums.EPaymentStatus;
import com.artShop.artShop.models.Address;
import com.artShop.artShop.models.Customer;
import com.artShop.artShop.models.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "ix_orders_customer", columnList = "customer_id"),
        @Index(name = "ix_orders_status_created", columnList = "payment_status, created_at"),
        @Index(name = "ix_orders_contact_email", columnList = "contact_email")
})
@Getter
@Setter
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private EPaymentStatus paymentStatus = EPaymentStatus.NEW;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 3)
    private String currencyCode;

    @Column(length = 64, unique = true)
    private String extOrderId;

    @Column(length = 64, unique = true)
    private String payuOrderId;

    @Column(length = 2000)
    private String redirectUri;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Embedded
    private Address shippingAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
            @AttributeOverride(name = "apartmentNumber", column = @Column(name = "billing_apartment_number")),
            @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
            @AttributeOverride(name = "state", column = @Column(name = "billing_state")),
            @AttributeOverride(name = "zip", column = @Column(name = "billing_zip")),
            @AttributeOverride(name = "country", column = @Column(name = "billing_country"))
    })
    private Address billingAddress;

    @Column(length = 255)
    private String contactEmail;

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}

