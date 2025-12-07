package com.artshop.backend.enums;

import lombok.Getter;

@Getter
public enum EPaymentStatus {
    NEW("NOWA"),
    PENDING("W TOKU"),
    COMPLETED("ZAKONCZONA"),
    FAILED("NIEUDANA");

    private final String descriptionPL;

    EPaymentStatus(String descriptionPL) {
        this.descriptionPL = descriptionPL;
    }

}
