package com.teamB.hospitalreservation.controller;

import com.teamB.hospitalreservation.service.HospitalDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/*
HospitalDataController
외부 소스로부터 병원 정보를 가져와 데이터베이스에 일괄적으로 저장(초기화)하는 역할을 합니다.
POST /api/hospital-data/init: 외부 API를 호출하여 대량의 병원 데이터를 가져와 DB에 저장하는 비동기 작업을 시작시킵니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospital-data")
public class HospitalDataController {

    private final HospitalDataService hospitalDataService;

    @PostMapping("/init")
    public ResponseEntity<Map<String, Object>> initializeAllHospitals() {
        try {
            log.info("병원 데이터 전체 초기화 요청을 접수했습니다. 비동기 작업으로 시작합니다.");
            hospitalDataService.fetchAndSaveAllHospitalData(); // 비동기 메서드 호출

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "병원 데이터 초기화가 백그라운드에서 시작되었습니다. 서버 로그를 통해 진행 상황을 확인하세요.");
            
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);

        } catch (Exception e) {
            log.error("병원 데이터 초기화 작업 시작 중 에러 발생", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "병원 데이터 초기화 작업을 시작하지 못했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}