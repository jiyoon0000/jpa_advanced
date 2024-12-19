package com.example.demo.enums;

import lombok.Generated;

public enum ReservationStatus {
    PENDING,
    APPROVED,
    CANCELED,
    EXPIRED;

    @Generated
    public boolean canTransitionTo(ReservationStatus newStatus){
        return switch (this){
            case PENDING -> newStatus == APPROVED || newStatus == CANCELED || newStatus == EXPIRED;
            case APPROVED -> newStatus == CANCELED;
            case CANCELED, EXPIRED -> false;
        };
    }
}
