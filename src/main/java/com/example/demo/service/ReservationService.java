package com.example.demo.service;

import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.*;
import com.example.demo.enums.ReservationStatus;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalLogService rentalLogService;
    private final JPAQueryFactory queryFactory;//QueryDsl 활용

    //의존성을 위한 생성자 정의
    public ReservationService(ReservationRepository reservationRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              RentalLogService rentalLogService,
                              JPAQueryFactory queryFactory) {
        this.reservationRepository = reservationRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.rentalLogService = rentalLogService;
        this.queryFactory = queryFactory;
    }

    // TODO: 1. 트랜잭션 이해
    @Transactional
    public ReservationResponseDto createReservation(Long itemId, Long userId, LocalDateTime startAt, LocalDateTime endAt) {
        // 쉽게 데이터를 생성하려면 아래 유효성검사 주석 처리
        List<Reservation> haveReservations = reservationRepository.findConflictingReservations(itemId, startAt, endAt);
        if (!haveReservations.isEmpty()) {
            throw new ReservationConflictException("해당 물건은 이미 그 시간에 예약이 있습니다.");
        }

        Item item = itemRepository.findByIdOrThrow(itemId);
        User user = userRepository.findByIdOrThrow(userId);
        Reservation reservation = new Reservation(item, user, "PENDING", startAt, endAt);
        Reservation savedReservation = reservationRepository.save(reservation);

        RentalLog rentalLog = new RentalLog(savedReservation, "로그 메세지", "CREATE");
        rentalLogService.save(rentalLog);

        return new ReservationResponseDto(
                savedReservation.getId(),
                savedReservation.getUser().getNickname(),
                savedReservation.getItem().getName(),
                savedReservation.getStartAt(),
                savedReservation.getEndAt()
        );
    }

    // TODO: 3. N+1 문제
    public List<ReservationResponseDto> getReservations() {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream().map(reservation -> {
            User user = reservation.getUser();
            Item item = reservation.getItem();

            return new ReservationResponseDto(
                    reservation.getId(),
                    user.getNickname(),
                    item.getName(),
                    reservation.getStartAt(),
                    reservation.getEndAt()
            );
        }).toList();
    }

    // TODO: 5. QueryDSL 검색 개선
    public List<ReservationResponseDto> searchAndConvertReservations(Long userId, Long itemId) {

        //userId와 itemId 조건에 따라 동적으로 검색
        List<Reservation> reservations = searchReservations(userId, itemId);

        //조회된 엔티티를 Dto로 변환하여 반환
        return convertToDto(reservations);
    }

    //QueryDSL을 사용한 동적 쿼리
    public List<Reservation> searchReservations(Long userId, Long itemId) {
        //QueryDSL에서 사용할 Q클래스 정의
        QReservation reservation = QReservation.reservation;
        QUser user = QUser.user;
        QItem item = QItem.item;

        //QueryDSL을 사용하여 동적 조건을 추가
        //fetchJoin을 활용해 N+1문제를 방지하고 연관된 데이터를 한번에 가져옴
        return queryFactory
                .selectFrom(reservation)
                .leftJoin(reservation.user, user).fetchJoin()
                .leftJoin(reservation.item, item).fetchJoin()
                .where(
                        userId != null ? reservation.user.id.eq(userId) : null,
                        itemId != null ? reservation.item.id.eq(itemId) : null
                )
                .fetch();
    }

    private List<ReservationResponseDto> convertToDto(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> new ReservationResponseDto(
                        reservation.getId(),
                        reservation.getUser().getNickname(),
                        reservation.getItem().getName(),
                        reservation.getStartAt(),
                        reservation.getEndAt()
                ))
                .toList();
    }

    // TODO: 7. 리팩토링
    @Transactional
    public Reservation updateReservationStatus(Long reservationId, String status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약 ID가 존재하지 않습니다."));

        ReservationStatus newStatus = ReservationStatus.valueOf(status.toUpperCase());
        reservation.updateStatus(newStatus);

        return reservation;
    }
}
