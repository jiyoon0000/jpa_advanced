package com.example.demo.enums;

public enum ReservationStatus {
    PENDING,
    APPROVED,
    CANCELED,
    EXPIRED;

    public boolean canTransitionTo(ReservationStatus newStatus){
        return switch (this){
            case PENDING -> newStatus == APPROVED || newStatus == CANCELED || newStatus == EXPIRED;
            case APPROVED -> newStatus == CANCELED;
            case CANCELED, EXPIRED -> false;
        };
    }
}
