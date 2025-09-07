package com.artshop.backend.enums;

import lombok.Getter;

@Getter
public enum EMediaImageType {
    JPEG("image/jpeg", "jpg"),
    PNG("image/png", "png");

    private final String mime;
    private final String extension;

    EMediaImageType(String mime, String extension) {
        this.mime = mime;
        this.extension = extension;
    }
}
