package com.teamB.hospitalreservation.service;

import com.teamB.hospitalreservation.dto.ReviewResponse;
import com.teamB.hospitalreservation.dto.TagReviewRequest;
import com.teamB.hospitalreservation.dto.TextReviewRequest;
import com.teamB.hospitalreservation.entity.*;
import com.teamB.hospitalreservation.repository.ReservationRepository;
import com.teamB.hospitalreservation.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    // 내 리뷰 목록 조회
    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReviews(Long userId) {
        return reviewRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }
    
    //  병원별 리뷰 목록 조회
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByHospital(Long hospitalId) {
        return reviewRepository.findAllByHospitalIdOrderByCreatedAtDesc(hospitalId).stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    // 병원별 평균 평점 조회
    @Transactional(readOnly = true)
    public double getAverageRating(Long hospitalId) {
        Double avg = reviewRepository.findAverageRating(hospitalId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    // 병원별 태그 통계 조회
    @Transactional(readOnly = true)
    public Map<ReviewTag, Long> getTagStatistics(Long hospitalId) {
        return reviewRepository.findAllByHospitalId(hospitalId).stream()
                .flatMap(review -> review.getTags().stream())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));
    }

    // 텍스트 리뷰 작성
    public ReviewResponse createTextReview(User user, TextReviewRequest request) {
        Reservation reservation = validateReservation(user, request.getReservationId());

        Review review = Review.builder()
                .reservation(reservation)
                .hospital(reservation.getHospital())
                .user(user)
                .reviewType(ReviewType.TEXT)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return ReviewResponse.from(reviewRepository.save(review));
    }

    // 태그 리뷰 작성
    public ReviewResponse createTagReview(User user, TagReviewRequest request) {
        Reservation reservation = validateReservation(user, request.getReservationId());

        Review review = Review.builder()
                .reservation(reservation)
                .hospital(reservation.getHospital())
                .user(user)
                .reviewType(ReviewType.TAG)
                .tags(new HashSet<>(request.getTags()))
                .rating(0)
                .comment("")
                .build();
        
        return ReviewResponse.from(reviewRepository.save(review));
    }

    // 텍스트 리뷰 수정
    public ReviewResponse updateTextReview(Long reviewId, TextReviewRequest request, User user) {
        Review review = validateReviewOwner(reviewId, user);
        if (review.getReviewType() != ReviewType.TEXT) {
            throw new IllegalArgumentException("텍스트 리뷰만 수정할 수 있습니다.");
        }
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return ReviewResponse.from(reviewRepository.save(review));
    }

    // 태그 리뷰 수정
    public ReviewResponse updateTagReview(Long reviewId, TagReviewRequest request, User user) {
        Review review = validateReviewOwner(reviewId, user);
        if (review.getReviewType() != ReviewType.TAG) {
            throw new IllegalArgumentException("태그 리뷰만 수정할 수 있습니다.");
        }
        review.setTags(new HashSet<>(request.getTags()));
        return ReviewResponse.from(reviewRepository.save(review));
    }

    // 리뷰 삭제
    public void deleteReview(Long reviewId, User user) {
        Review review = validateReviewOwner(reviewId, user);
        reviewRepository.delete(review);
    }

    // 예약 정보 및 소유권 검증
    private Reservation validateReservation(User user, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약 정보를 찾을 수 없습니다. ID: " + reservationId));
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new SecurityException("예약자 본인만 리뷰를 작성할 수 있습니다.");
        }
        if (reviewRepository.existsByReservation(reservation)) {
            throw new IllegalStateException("이미 리뷰가 작성된 예약입니다.");
        }
        return reservation;
    }

    // 리뷰 소유권 검증
    private Review validateReviewOwner(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. ID: " + reviewId));
        if (!review.getUser().getId().equals(user.getId())) {
            throw new SecurityException("리뷰 작성자 본인만 수정/삭제할 수 있습니다.");
        }
        return review;
    }
}