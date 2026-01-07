package com.teamB.hospitalreservation.controller;

import com.teamB.hospitalreservation.dto.HospitalRequestDto;
import com.teamB.hospitalreservation.dto.HospitalResponseDto;
import com.teamB.hospitalreservation.service.HospitalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
HospitalController
사용자가 원하는 조건에 맞는 병원을 검색하는 기능을 제공합니다.
GET /api/hospitals/search: 시/도, 시/군/구, 진료과목 등의 검색 조건(쿼리 파라미터)을 받아 조건에 맞는 병원 목록을 반환합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospitals")
public class HospitalController {
    private final HospitalSearchService hospitalSearchService;

    @GetMapping("/search")
    public List<HospitalResponseDto> searchHospitals(
            @RequestParam(value = "sidoCode", required = false) String sidoCode,
            @RequestParam(value = "sgguCode", required = false) String sgguCode,
            @RequestParam(value = "departmentCode", required = false) String subjectName) {

        HospitalRequestDto requestDto = new HospitalRequestDto();
        requestDto.setSidoCode(sidoCode);
        requestDto.setSgguCode(sgguCode);
        requestDto.setSubjectCode(subjectName);

        return hospitalSearchService.search(requestDto);
    }

    @PostMapping
    public ResponseEntity<String> createHospital(@RequestBody HospitalRequestDto requestDto) {
        return ResponseEntity.ok("Hospital creation endpoint.");
    }
}