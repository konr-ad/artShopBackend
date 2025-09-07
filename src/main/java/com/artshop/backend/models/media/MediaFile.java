package com.artshop.backend.models.media;

import com.artshop.backend.models.base.BaseEntity;
import com.artshop.backend.models.entity.Painting;
import com.artshop.backend.enums.EMediaImageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "media_files", indexes = {
        @Index(name="ix_media_painting_primary", columnList="painting_id, is_primary, sort_order")
})
@Getter @Setter
public class MediaFile extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="painting_id", nullable=false)
    private Painting painting;

    @Column(nullable=false, length=512)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable=false, length=8)
    private EMediaImageType type;

    @Column(nullable=false)
    private boolean isPrimary = false;

    @Column(nullable=false)
    private int sortOrder = 0;

    @Column(length=255)
    private String originalFilename;
}