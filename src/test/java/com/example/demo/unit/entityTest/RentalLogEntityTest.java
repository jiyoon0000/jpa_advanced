package com.example.demo.unit.entityTest;

import com.example.demo.entity.RentalLog;
import com.example.demo.entity.Reservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RentalLogEntityTest {

    @Test
    @DisplayName("RentalLog 생성 시 필드 값 확인")
    void rentalLogFiled(){
        //given
        Reservation mockReservation = new Reservation();
        String logMessage = "Reservation created successfully";
        String logType = "SUCCESS";

        //when
        RentalLog rentalLog = new RentalLog(mockReservation, logMessage, logType);

        //then
        assertThat(rentalLog.getReservation()).isEqualTo(mockReservation);
        assertThat(rentalLog.getLogMessage()).isEqualTo(logMessage);
        assertThat(rentalLog.getLogType()).isEqualTo(logType);
    }

    @Test
    @DisplayName("RentalLog 기본 생성자 확인")
    void rentalLogDefault(){
        //when
        RentalLog rentalLog = new RentalLog();

        //then
        assertThat(rentalLog.getId()).isNull();
        assertThat(rentalLog.getLogMessage()).isNull();
        assertThat(rentalLog.getLogType()).isNull();
        assertThat(rentalLog.getReservation()).isNull();
    }

    @Test
    @DisplayName("RentalLog 예약 설정")
    void rentalLogReservation(){
        //given
        Reservation mockReservation = new Reservation();

        //when
        RentalLog rentalLog = new RentalLog(mockReservation, "reservation", "SUCCESS");

        //then
        assertThat(rentalLog.getReservation()).isEqualTo(mockReservation);
    }
}
