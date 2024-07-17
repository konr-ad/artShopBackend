package com.artShop.artShop.models.payu;

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

    private String email;
    private String firstName;
    private String lastName;
    private String country;
    private String state;
    private String address;
    private String apartmentNumber;
    private String city;
    private String zip;
    private String description;
    private String currencyCode;
    private String totalAmount;
    private String extOrderId;
    private String status;
    private String redirectUri;
    private String payuOrderId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderItem> items = new ArrayList<>();

}
