package com.teamB.hospitalreservation.dto;

import com.teamB.hospitalreservation.entity.ReviewTag;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TagReviewRequest {
    private Long reservationId;
    private List<ReviewTag> tags;
}