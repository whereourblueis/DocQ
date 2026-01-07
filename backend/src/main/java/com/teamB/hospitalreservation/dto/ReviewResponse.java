package com.teamB.hospitalreservation.dto;

import com.teamB.hospitalreservation.entity.Review;
import com.teamB.hospitalreservation.entity.ReviewTag;
import com.teamB.hospitalreservation.entity.ReviewType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
public class ReviewResponse {
    
    private Long id;
    private String writer;
    private String hospitalName;
    private Integer rating;
    private String comment;
    private Set<ReviewTag> tags;
    private ReviewType reviewType;
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId()) //
                .writer(review.getUser().getName())
                .hospitalName(review.getHospital().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .tags(review.getTags())
                .reviewType(review.getReviewType())
                .createdAt(review.getCreatedAt())
                .build();
    }
}