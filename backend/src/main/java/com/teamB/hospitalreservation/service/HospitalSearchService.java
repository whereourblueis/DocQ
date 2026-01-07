package com.teamB.hospitalreservation.service;

import com.teamB.hospitalreservation.dto.HospitalRequestDto;
import com.teamB.hospitalreservation.dto.HospitalResponseDto;
import com.teamB.hospitalreservation.entity.Hospital;
import com.teamB.hospitalreservation.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalSearchService {

    private final HospitalRepository hospitalRepository;

    @Transactional(readOnly = true)
    public List<HospitalResponseDto> search(HospitalRequestDto hospitalRequestDto) {
        String sidoCode = hospitalRequestDto.getSidoCode();
        String sgguCode = hospitalRequestDto.getSgguCode();
        String nameKeyword = hospitalRequestDto.getSubjectCode();

        List<Hospital> hospitals;

        boolean hasLocation = StringUtils.hasText(sidoCode) && StringUtils.hasText(sgguCode);
        boolean hasNameKeyword = StringUtils.hasText(nameKeyword);

        if (hasLocation && hasNameKeyword) {
            hospitals = hospitalRepository.findBySidoCodeAndSgguCodeAndNameContaining(sidoCode, sgguCode, nameKeyword);
        } else if (hasLocation) {
            hospitals = hospitalRepository.findBySidoCodeAndSgguCode(sidoCode, sgguCode);
        } else if (hasNameKeyword) {
            hospitals = hospitalRepository.findByNameContaining(nameKeyword);
        } else {
            hospitals = hospitalRepository.findAll();
        }

        return hospitals.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private HospitalResponseDto toDto(Hospital hospital) {
        return new HospitalResponseDto(
                hospital.getId(),
                hospital.getName(),
                hospital.getAddress(),
                hospital.getPhone(),
                hospital.getApiId(),
                hospital.getSidoCode(),
                hospital.getSgguCode()
        );
    }
}