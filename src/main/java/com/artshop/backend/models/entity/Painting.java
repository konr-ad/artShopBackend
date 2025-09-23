package com.artshop.backend.models.entity;

import com.artshop.backend.enums.EPaintingState;
import com.artshop.backend.enums.EPaintingType;
import com.artshop.backend.models.base.BaseEntity;
import com.artshop.backend.models.media.MediaFile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paintings", indexes = {
        @Index(name = "ix_paintings_type", columnList = "type"),
        @Index(name = "ix_paintings_state", columnList = "state")
})
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"media"})
public class Painting extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EPaintingType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private EPaintingState state;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String descriptionPl;

    @Column(columnDefinition = "TEXT")
    private String descriptionEn;

    @PositiveOrZero
    @Column(nullable = false)
    private int quantity = 1;

    @OneToMany(mappedBy = "painting", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("isPrimary DESC, sortOrder ASC, id ASC")
    private List<MediaFile> media = new ArrayList<>();

    public void addMedia(MediaFile mf, boolean primary, int sortOrder) {
        mf.setPainting(this);
        mf.setPrimary(primary);
        mf.setSortOrder(sortOrder);
        media.add(mf);
    }
}

