package com.artShop.artShop.models.payu;

import com.artShop.artShop.models.Customer;
import com.artShop.artShop.models.Painting;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String description;

    @Column(length = 255)
    private String currencyCode;

    @Column(length = 255)
    private String totalAmount;

    @Column(length = 255)
    private String extOrderId;

    @Column(length = 255)
    private String paymentStatus;

    @Column(length = 2000)
    private String redirectUri;

    @Column(length = 255)
    private String payuOrderId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order")
    private List<Painting> paintings = new ArrayList<>();
}
