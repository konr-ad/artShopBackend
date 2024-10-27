package com.artShop.artShop.models.payu;

import com.artShop.artShop.models.Customer;
import com.artShop.artShop.models.Painting;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
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

    @CreationTimestamp
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Painting> paintings = new ArrayList<>();

    public void addPainting(Painting painting) {
        this.paintings.add(painting);
        painting.setOrder(this);
    }

    public void removePainting(Painting painting) {
        this.paintings.remove(painting);
        painting.setOrder(null);
    }
}
