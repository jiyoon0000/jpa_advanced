package com.example.demo.unit.serviceTest;

import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Item;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.enums.ReservationStatus;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.RentalLogService;
import com.example.demo.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    //테스트할 객체를 생성하고, mock 객체로 의존성 주입
    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RentalLogService rentalLogService;

    private User mockUser;
    private Item mockItem;
    private Reservation mockReservation;

    //기본 테스트 데이터 초기화, mock 객체를 생성하여 재사용
    @BeforeEach
    void setup(){
        mockUser = new User("USER", "test@test.com", "testNickname", "password123");
        mockItem = new Item("Test Item", "Item Description", mockUser, mockUser);
        mockReservation = new Reservation(mockItem, mockUser, "PENDING", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("예약 생성 단위 테스트 성공")
    void createReservationSuccess(){
        //given
        when(itemRepository.findByIdOrThrow(anyLong())).thenReturn(mockItem);
        when(userRepository.findByIdOrThrow(anyLong())).thenReturn(mockUser);
        when(reservationRepository.findConflictingReservations(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(mockReservation);

        //when
        ReservationResponseDto response = reservationService.createReservation(1L, 1L, LocalDateTime.now(), LocalDateTime.now());

        //then
        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo("testNickname");
        assertThat(response.getItemName()).isEqualTo("Test Item");
    }

    @Test
    @DisplayName("예약 생성 단위 테스트 실패")
    void createReservationTimeConflict() {
        // Given : Mock 설정 - 시간 충돌 발생
        when(reservationRepository.findConflictingReservations(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(mockReservation));

        // When & Then : 예외 발생 여부 확인
        assertThrows(ReservationConflictException.class, () -> {
            reservationService.createReservation(1L, 1L, LocalDateTime.now(), LocalDateTime.now());
        });
    }

    @Test
    @DisplayName("예약 상태 업데이트 단위 테스트")
    void updateReservationStatusSuccess() {
        // Given : 예약 반환
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(mockReservation));

        // When : 상태 호출 후 상태값 검증
        Reservation updatedReservation = reservationService.updateReservationStatus(1L, "APPROVED");

        // Then
        assertThat(updatedReservation.getStatus()).isEqualTo(ReservationStatus.APPROVED);
    }

    @Test
    @DisplayName("예약 상태 업데이트 단위 테스트 - 존재하지 않는 예약")
    void updateReservationStatus_NotFound() {
        // Given
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservationStatus(1L, "APPROVED");
        });
    }
}
