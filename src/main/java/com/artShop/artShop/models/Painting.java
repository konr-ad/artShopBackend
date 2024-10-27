package com.artShop.artShop.models;

import com.artShop.artShop.enums.EPaintingState;
import com.artShop.artShop.enums.EPaintingType;
import com.artShop.artShop.models.payu.Order;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paintings")
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
    private EPaintingState state;
    @Positive(message = "Price should be positive")
    private double price;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String quantity;

    @Lob
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @OneToMany(mappedBy = "painting", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AdditionalImage> additionalImages = new ArrayList<>();

    public void addAdditionalImage(AdditionalImage additionalImage) {
        additionalImages.add(additionalImage);
        additionalImage.setPainting(this);
    }

    public void removeAdditionalImage(AdditionalImage additionalImage) {
        additionalImages.remove(additionalImage);
        additionalImage.setPainting(null);
    }
}
