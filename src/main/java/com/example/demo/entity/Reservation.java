package com.example.demo.entity;

import com.example.demo.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING; // PENDING, APPROVED, CANCELED, EXPIRED

    public Reservation(Item item, User user, String status, LocalDateTime startAt, LocalDateTime endAt) {
        this.item = item;
        this.user = user;
        this.status = ReservationStatus.valueOf(status);
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public void updateStatus(ReservationStatus newStatus) {
        if(!status.canTransitionTo(newStatus)){
            throw new IllegalArgumentException("현재 상태에서" + newStatus + "로 변경할 수 없습니다.");
        }
        this.status = newStatus;
    }
}
