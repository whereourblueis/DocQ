package com.teamB.hospitalreservation.service;

import com.teamB.hospitalreservation.config.LocationDataProvider;
import com.teamB.hospitalreservation.entity.Location;
import com.teamB.hospitalreservation.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationDataService {

    private final LocationRepository locationRepository;
    private final LocationDataProvider locationDataProvider;

    @Transactional
    public void fetchAndSaveLocationData() {
        if (locationRepository.count() > 0) {
            log.info("Location data already exists ({} records). Skipping initialization.", locationRepository.count());
            return;
        }

        log.info("Starting to initialize location data with hardcoded values...");
        
        try {
            List<Location> locations = locationDataProvider.getHardcodedLocations();
            
            locationRepository.saveAll(locations);
            
            log.info("Successfully saved {} locations to the database.", locations.size());
            
        } catch (Exception e) {
            log.error("Failed to save hardcoded location data", e);
            throw new RuntimeException("Location 데이터 초기화 실패", e);
        }
    }
}