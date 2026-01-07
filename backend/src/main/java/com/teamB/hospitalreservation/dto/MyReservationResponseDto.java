package com.teamB.hospitalreservation.dto;

import com.teamB.hospitalreservation.entity.Reservation;
import com.teamB.hospitalreservation.entity.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyReservationResponseDto {
    private Long reservationId;
    private String hospitalName;
    private String subjectName;
    private LocalDateTime reservationTime;
    private ReservationStatus status;

    public static MyReservationResponseDto from(Reservation reservation) {
        return MyReservationResponseDto.builder()
                .reservationId(reservation.getId())
                .hospitalName(reservation.getHospital().getName())
                .subjectName(reservation.getSubject().getName())
                .reservationTime(reservation.getReservationTime())
                .status(reservation.getStatus())
                .build();
    }
}
