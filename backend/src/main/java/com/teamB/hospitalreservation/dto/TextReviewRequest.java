package com.teamB.hospitalreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TextReviewRequest {
    private Long reservationId;
    private int rating;
    private String comment;
}