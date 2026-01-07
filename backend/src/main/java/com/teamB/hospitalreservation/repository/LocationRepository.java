package com.teamB.hospitalreservation.repository;

import com.teamB.hospitalreservation.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findBySidoCodeAndSgguCode(String sidoCode, String sgguCode);

    boolean existsBySidoCodeAndSgguCode(String sidoCode, String sgguCode);
}