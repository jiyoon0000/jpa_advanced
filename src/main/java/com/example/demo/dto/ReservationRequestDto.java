package com.example.demo.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationRequestDto {
    @NotNull
    private Long itemId;

    @NotNull
    private Long userId;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;

    public ReservationRequestDto(Long itemId, Long userId, LocalDateTime startAt, LocalDateTime endAt) {
        this.itemId = itemId;
        this.userId = userId;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
