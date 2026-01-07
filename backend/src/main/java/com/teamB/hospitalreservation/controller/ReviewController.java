package com.teamB.hospitalreservation.controller;

import com.teamB.hospitalreservation.config.UserPrincipal;
import com.teamB.hospitalreservation.dto.ReviewResponse;
import com.teamB.hospitalreservation.dto.TagReviewRequest;
import com.teamB.hospitalreservation.dto.TextReviewRequest;
import com.teamB.hospitalreservation.entity.ReviewTag;
import com.teamB.hospitalreservation.entity.User;
import com.teamB.hospitalreservation.repository.UserRepository;
import com.teamB.hospitalreservation.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 리뷰 관련 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    /**
     * 현재 로그인한 사용자가 작성한 모든 리뷰를 조회합니다.
     */
    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ReviewResponse> myReviews = reviewService.getMyReviews(userPrincipal.getId());
        return ResponseEntity.ok(myReviews);
    }
    
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<ReviewResponse>> getHospitalReviews(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(reviewService.getReviewsByHospital(hospitalId));
    }

    @GetMapping("/hospital/{hospitalId}/average")
    public ResponseEntity<Double> getAverage(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(reviewService.getAverageRating(hospitalId));
    }

    @GetMapping("/hospital/{hospitalId}/tags")
    public ResponseEntity<Map<ReviewTag, Long>> getTags(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(reviewService.getTagStatistics(hospitalId));
    }

    @PostMapping("/text")
    public ResponseEntity<String> writeTextReview(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody TextReviewRequest request) {

        if (userPrincipal == null) {
            return ResponseEntity.status(401).body("인증된 사용자만 리뷰를 작성할 수 있습니다.");
        }

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));

        reviewService.createTextReview(user, request);
        return ResponseEntity.ok("텍스트 리뷰가 등록되었습니다.");
    }

    @PostMapping("/tag")
    public ResponseEntity<String> writeTagReview(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody TagReviewRequest request) {

        if (userPrincipal == null) {
            return ResponseEntity.status(401).body("인증된 사용자만 리뷰를 작성할 수 있습니다.");
        }

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));

        reviewService.createTagReview(user, request);
        return ResponseEntity.ok("간편 리뷰가 등록되었습니다.");
    }

    @PutMapping("/text/{reviewId}")
    public ResponseEntity<String> updateTextReview(@PathVariable Long reviewId,
                                                   @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                   @RequestBody TextReviewRequest request) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));
        reviewService.updateTextReview(reviewId, request, user);
        return ResponseEntity.ok("텍스트 리뷰가 수정되었습니다.");
    }

    @PutMapping("/tag/{reviewId}")
    public ResponseEntity<String> updateTagReview(@PathVariable Long reviewId,
                                                  @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @RequestBody TagReviewRequest request) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));
        reviewService.updateTagReview(reviewId, request, user);
        return ResponseEntity.ok("태그 리뷰가 수정되었습니다.");
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));
        reviewService.deleteReview(reviewId, user);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }
}