package com.teamB.hospitalreservation.repository;

import com.teamB.hospitalreservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 병원의 특정 날짜의 예약 정보 조회
    List<Reservation> findByHospitalIdAndReservationTimeBetween(Long hospitalId, LocalDateTime start, LocalDateTime end);

    // [추가] 병원, 진료과목, 특정 날짜의 예약 정보 조회
    List<Reservation> findByHospital_IdAndSubject_NameAndReservationTimeBetween(Long hospitalId, String subjectName, LocalDateTime start, LocalDateTime end);

    // 병원, 특정 시간대의 예약 여부 확인
    boolean existsByHospitalIdAndReservationTime(Long hospitalId, LocalDateTime reservationTime);

    List<Reservation> findByUserId(Long userId);

}