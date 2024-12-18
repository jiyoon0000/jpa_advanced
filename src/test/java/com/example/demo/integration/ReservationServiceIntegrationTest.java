package com.example.demo.integration;

import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Item;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.enums.ReservationStatus;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest //애플리케이션 환경과 동일하게 테스트 실행
@Transactional //테스트 실행 후 데이터베이스에 입력된 데이터가 롤백되도록 보장
class ReservationServiceIntegrationTest {

    //의존성 주입
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("예약 생성 성공")
    void createReservation(){
        //given
        User user = userRepository.save(new User("USER", "test@test.com", "testNickname","password123"));
        Item item = itemRepository.save(new Item("Test Item", "Item Description", user, user));

        Long userId = user.getId();
        Long itemId = item.getId();
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = LocalDateTime.now();

        //when : 예약 생성 서비스 메서드 호출
        ReservationResponseDto responseDto = reservationService.createReservation(itemId, userId, startAt, endAt);

        //then : 반환된 값이 null이 아닌지, 예약한 사용자의 닉네임이 일치하는지, 시작&종료시간 검증
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getNickname()).isEqualTo("testNickname");
        assertThat(responseDto.getStartAt()).isEqualTo(startAt);
        assertThat(responseDto.getEndAt()).isEqualTo(endAt);
    }

    @Test
    @DisplayName("예약 생성 실패")
    void createReservationFail(){
        //given : 예약이 이미 존재하는 상황 설정
        User user = userRepository.save(new User("USER", "test@test.com", "testNickname","password123"));
        Item item = itemRepository.save(new Item("Test Item", "Item Description", user, user));

        Long userId = user.getId();
        Long itemId = item.getId();
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = LocalDateTime.now();

        //기존 예약 데이터 삽입
        reservationRepository.save(new Reservation(item,user,"APPROVED", startAt, endAt));

        //when, then : 예외 발생 확인
        Exception exception = assertThrows(ReservationConflictException.class, () -> reservationService.createReservation(itemId,userId,startAt,endAt));
        assertThat(exception.getMessage()).isEqualTo("해당 물건은 이미 그 시간에 예약이 있습니다.");
    }

    @Test
    @DisplayName("예약 상태 업데이트")
    void updateReservationStatus(){
        //given : 예약 데이터 입력
        User user = userRepository.save(new User("USER", "test@test.com", "testNickname","password123"));
        Item item = itemRepository.save(new Item("Test Item", "Item Description", user, user));

        Long userId = user.getId();
        Long itemId = item.getId();
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = LocalDateTime.now();

        //예약 데이터 저장
        Reservation reservation = reservationRepository.save(new Reservation(item, user, "PENDING", startAt, endAt));
        Long reservationId = reservation.getId();

        //when : 예약 상태 업데이트
        Reservation updatedReservation = reservationService.updateReservationStatus(reservationId, "APPROVED");

        //then : 상태 변경 검증
        assertThat(updatedReservation.getStatus()).isEqualTo(ReservationStatus.APPROVED);
    }
}
