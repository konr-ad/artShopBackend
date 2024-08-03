package com.artShop.artShop.models;

import com.artShop.artShop.enums.EPaintingState;
import com.artShop.artShop.enums.EPaintingType;
import com.artShop.artShop.models.payu.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "painting")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Painting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required")
    private String name;
    @NotNull(message = "Type is required")
    private EPaintingType type;
    @NotNull(message = "State is required")
    private EPaintingState state;
    @Positive(message = "Price should be positive")
    private double price;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String quantity;

    @Lob
    private byte[] image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "painting")
    private List<AdditionalImage> additionalImages = new ArrayList<>();

}
