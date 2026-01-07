package com.teamB.hospitalreservation.controller;

import com.teamB.hospitalreservation.dto.MyReservationResponseDto;
import com.teamB.hospitalreservation.dto.ReservationRequestDto;
import com.teamB.hospitalreservation.dto.ReservationResponseDto;
import com.teamB.hospitalreservation.repository.HospitalRepository;
import com.teamB.hospitalreservation.config.UserPrincipal;
import com.teamB.hospitalreservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
/*
ReservationController
병원 예약과 관련된 모든 기능을 처리하는 핵심 컨트롤러입니다.
POST /api/reservations: 특정 병원에 대한 예약을 생성합니다.(인증된 사용자만 가능)
GET /api/reservations/my: 현재 로그인한 사용자의 모든 예약 내역을 조회합니다.
GET /api/reservations/booked-times:: 특정 병원, 날짜, 진료과에 이미 예약이 완료된 시간 목록을 조회하여, 사용자가 중복 예약을 할 수 없도록 합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final HospitalRepository hospitalRepository;

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequestDto requestDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자만 접근할 수 있습니다.");
        }

        ReservationResponseDto reservationDto = reservationService.createReservation(requestDto, userPrincipal.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationDto);
    }


     // 특정 병원, 진료과, 날짜에 예약된 시간 목록을 조회합니다.

    @GetMapping("/booked-times")
    public ResponseEntity<List<String>> getBookedTimes(
            @RequestParam Long hospitalId,
            @RequestParam("subject") String subjectName,
            @RequestParam String date) {
        try {
            LocalDate targetDate = LocalDate.parse(date);
            List<String> bookedTimes = reservationService.getBookedTimes(hospitalId, subjectName, targetDate);
            return ResponseEntity.ok(bookedTimes);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 날짜 형식입니다. (YYYY-MM-DD)");
        }
    }

    @GetMapping("/{hospitalId}/available-times")
    public ResponseEntity<?> getAvailableTimes(@PathVariable Long hospitalId, @RequestParam String date) {
        boolean exists = hospitalRepository.existsById(hospitalId);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("오류: 병원 ID " + hospitalId + "를 찾을 수 없습니다.");
        }

        try {
            LocalDate targetDate = LocalDate.parse(date);
            Map<String, Boolean> availableTimes = reservationService.getAvailableTimes(hospitalId, targetDate);
            return ResponseEntity.ok(availableTimes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("잘못된 날짜 형식입니다. (YYYY-MM-DD)");
        }
    }
    @GetMapping("/my")
    public ResponseEntity<List<MyReservationResponseDto>> getMyReservations(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자만 접근할 수 있습니다.");
        }
        // [수정] 반환 타입을 서비스와 일치시킵니다.
        List<MyReservationResponseDto> myReservations = reservationService.getMyReservations(userPrincipal.getId());
        return ResponseEntity.ok(myReservations);
    }
}