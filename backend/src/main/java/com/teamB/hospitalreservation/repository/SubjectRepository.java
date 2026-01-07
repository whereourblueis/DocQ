package com.teamB.hospitalreservation.repository;

import com.teamB.hospitalreservation.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // [추가] 진료과목 이름으로 Subject 엔티티를 찾기 위한 메소드
    Optional<Subject> findByName(String name);
}