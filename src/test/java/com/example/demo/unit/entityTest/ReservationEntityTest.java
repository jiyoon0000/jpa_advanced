package com.example.demo.unit.entityTest;

import com.example.demo.entity.Item;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.enums.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservationEntityTest {

    @Test
    @DisplayName("Reservation 생성시 필드 초기화 확인")
    void reservationInitialization() {
        //given
        User mockUser = new User("USER", "test@test.com", "TestUser", "password123");
        Item mockItem = new Item("TestItem", "ItemDescription", mockUser, mockUser);
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = LocalDateTime.now();

        //when
        Reservation reservation = new Reservation(mockItem, mockUser, "PENDING", startAt, endAt);

        //then
        assertThat(reservation.getItem()).isEqualTo(mockItem);
        assertThat(reservation.getUser()).isEqualTo(mockUser);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(reservation.getStartAt()).isEqualTo(startAt);
        assertThat(reservation.getEndAt()).isEqualTo(endAt);
    }

    @Test
    @DisplayName("Reservation 상태 업데이트 성공")
    void reservationStatusUpdateSuccess(){
        //given
        User mockUser = new User("USER", "test@test.com", "TestUser", "password123");
        Item mockItem = new Item("TestItem", "ItemDescription", mockUser, mockUser);
        Reservation reservation = new Reservation(mockItem, mockUser, "PENDING", LocalDateTime.now(), LocalDateTime.now());

        //when
        reservation.updateStatus(ReservationStatus.APPROVED);

        //then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.APPROVED);
    }

    @Test
    @DisplayName("Reservation 상태 업데이트 실패")
    void reservationStatusUpdateFail(){
        //given
        User mockUser = new User("USER", "test@test.com", "TestUser", "password123");
        Item mockItem = new Item("TestItem", "ItemDescription", mockUser, mockUser);
        Reservation reservation = new Reservation(mockItem, mockUser, "PENDING", LocalDateTime.now(), LocalDateTime.now());

        //when, then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> reservation.updateStatus(ReservationStatus.PENDING));

        assertThat(exception.getMessage()).contains("변경할 수 없습니다.");
    }
}

