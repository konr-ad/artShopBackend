package com.artShop.artShop.models.payu;

import com.artShop.artShop.models.Customer;
import com.artShop.artShop.models.Painting;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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

    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Painting> paintings = new ArrayList<>();
}
