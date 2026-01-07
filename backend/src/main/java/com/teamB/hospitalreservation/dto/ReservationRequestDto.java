package com.teamB.hospitalreservation.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationRequestDto {
    private Long hospitalId;
    private String subjectName;
    private LocalDateTime reservationTime;
    private String phoneNumber; // [추가] 예약자 전화번호 필드
}