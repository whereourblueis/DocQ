package com.teamB.hospitalreservation.service;

import com.teamB.hospitalreservation.api.HospitalApiClient;
import com.teamB.hospitalreservation.dto.HospitalResponseDto;
import com.teamB.hospitalreservation.entity.Location;
import com.teamB.hospitalreservation.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class HospitalDataService {

    private final LocationRepository locationRepository;
    private final HospitalApiClient hospitalApiClient;
    private final HospitalService hospitalService;

    @Async
    public void fetchAndSaveAllHospitalData() {
        List<Location> locations = locationRepository.findAll();
        log.info("Total locations to process: {}", locations.size());
        AtomicInteger processedCount = new AtomicInteger(0);

     // 검색 속도 개선
        locations.parallelStream().forEach(location -> {
            try {
                log.info("Fetching hospitals for location: {} ({}, {})", location.getName(), location.getSidoCode(), location.getSgguCode());
                List<HospitalResponseDto> hospitalDtos = hospitalApiClient.callApi(location.getSidoCode(), location.getSgguCode(), null);

                if (hospitalDtos != null && !hospitalDtos.isEmpty()) {
                    hospitalService.saveHospitalDataBatch(hospitalDtos, location);
                    log.info("Saved/updated {} hospitals for location {}", hospitalDtos.size(), location.getName());
                } else {
                    log.info("No hospitals found for location {}", location.getName());
                }
            } catch (Exception e) {
                log.error("Error processing location {}: {}", location.getName(), e.getMessage(), e);
            }
            log.info("Processed {}/{} locations.", processedCount.incrementAndGet(), locations.size());
        });

        log.info("Finished fetching and saving all hospital data.");
    }
}