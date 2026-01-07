package com.teamB.hospitalreservation.repository;

import com.teamB.hospitalreservation.entity.Reservation;
import com.teamB.hospitalreservation.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByReservation(Reservation reservation);

    List<Review> findAllByHospitalId(Long hospitalId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.hospital.id = :hospitalId")
    Double findAverageRating(@Param("hospitalId") Long hospitalId);

    List<Review> findAllByHospitalIdOrderByCreatedAtDesc(Long hospitalId);

    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.tags WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Review> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}