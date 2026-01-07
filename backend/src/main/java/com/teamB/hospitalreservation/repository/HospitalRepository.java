package com.teamB.hospitalreservation.repository;

import com.teamB.hospitalreservation.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    Optional<Hospital> findByApiId(String apiId);

    List<Hospital> findByApiIdIn(List<String> apiIds);


    List<Hospital> findBySidoCodeAndSgguCode(String sidoCode, String sgguCode);

    List<Hospital> findBySidoCodeAndSgguCodeAndNameContaining(String sidoCode, String sgguCode, String name);

    List<Hospital> findByNameContaining(String name);
}