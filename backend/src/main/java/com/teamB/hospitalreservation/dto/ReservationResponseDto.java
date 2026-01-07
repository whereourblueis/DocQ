package com.teamB.hospitalreservation.dto;

import com.teamB.hospitalreservation.entity.Reservation;
import com.teamB.hospitalreservation.entity.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationResponseDto {
    private Long reservationId;
    private String hospitalName;
    private String subjectName;
    private LocalDateTime reservationTime;
    private String phoneNumber;
    private ReservationStatus status;

    public static ReservationResponseDto from(Reservation reservation) {
        return ReservationResponseDto.builder()
                .reservationId(reservation.getId())
                .hospitalName(reservation.getHospital().getName())
                .subjectName(reservation.getSubject().getName())
                .reservationTime(reservation.getReservationTime())
                .phoneNumber(reservation.getPhoneNumber())
                .status(reservation.getStatus())
                .build();
    }
}