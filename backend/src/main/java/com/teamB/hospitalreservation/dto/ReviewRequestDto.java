package com.teamB.hospitalreservation.dto;

import com.teamB.hospitalreservation.entity.ReviewTag;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ReviewRequestDto {
    private int rating;
    private String content;
    private Set<ReviewTag> tags;
}