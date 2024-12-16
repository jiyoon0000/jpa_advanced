package com.example.demo.controller;

import com.example.demo.dto.ReservationRequestDto;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Reservation;
import com.example.demo.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody ReservationRequestDto reservationRequestDto) {
        ReservationResponseDto reservation = reservationService.createReservation(
                reservationRequestDto.getItemId(),
                reservationRequestDto.getUserId(),
                reservationRequestDto.getStartAt(),
                reservationRequestDto.getEndAt());

        return ResponseEntity.status(201).body(reservation);
    }

    @PatchMapping("/{id}/update-status")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody String status) {
        Reservation updatedReservation = reservationService.updateReservationStatus(id, status);
        return ResponseEntity.ok(updatedReservation);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> findAll() {
        List<ReservationResponseDto> reservations = reservationService.getReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponseDto>> searchAll(@RequestParam(required = false) Long userId,
                                                       @RequestParam(required = false) Long itemId) {
        List<ReservationResponseDto> reservations = reservationService.searchAndConvertReservations(userId, itemId);
        return ResponseEntity.ok(reservations);
    }
}
