package com.teamB.hospitalreservation.dto;

import com.teamB.hospitalreservation.entity.Reservation;
import com.teamB.hospitalreservation.entity.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private String hospitalName;
    private String subjectName;
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reservationTime;
    private ReservationStatus status;

    public static ReservationDTO fromEntity(Reservation reservation) {
        return ReservationDTO.builder()
                .id(reservation.getId())
                .hospitalName(reservation.getHospital().getName())
                .subjectName(reservation.getSubject().getName())
                .reservationTime(reservation.getReservationTime())
                .status(reservation.getStatus())
                .build();
    }
}