package com.teamB.hospitalreservation.repository;

import com.teamB.hospitalreservation.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
}