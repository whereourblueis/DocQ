package com.teamB.hospitalreservation.service;

import com.teamB.hospitalreservation.dto.HospitalRequestDto;
import com.teamB.hospitalreservation.dto.HospitalResponseDto;
import com.teamB.hospitalreservation.entity.Hospital;
import com.teamB.hospitalreservation.entity.Location;
import com.teamB.hospitalreservation.entity.Subject;
import com.teamB.hospitalreservation.repository.HospitalRepository;
import com.teamB.hospitalreservation.repository.LocationRepository;
import com.teamB.hospitalreservation.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final LocationRepository locationRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public void upsertHospital(HospitalRequestDto requestDto) {
        String ykiho = requestDto.getApiId();
        if (ykiho == null || ykiho.isBlank()) {
            return;
        }

        Location location = locationRepository.findBySidoCodeAndSgguCode(
                requestDto.getSidoCode(),
                requestDto.getSgguCode()
        ).orElseThrow(() -> new EntityNotFoundException("유효하지 않은 지역 코드입니다: " + requestDto.getSidoCode() + ", " + requestDto.getSgguCode()));

        Optional<Hospital> existingHospitalOpt = hospitalRepository.findByApiId(ykiho);
        String phone = (requestDto.getPhone() == null || requestDto.getPhone().isBlank()) ? "정보 없음" : requestDto.getPhone();

        if (existingHospitalOpt.isPresent()) {
            Hospital hospitalToUpdate = existingHospitalOpt.get();
            hospitalToUpdate.setName(requestDto.getName());
            hospitalToUpdate.setAddress(requestDto.getAddress());
            hospitalToUpdate.setPhone(phone);
            hospitalToUpdate.setLocation(location);
            hospitalToUpdate.setSidoCode(location.getSidoCode());
            hospitalToUpdate.setSgguCode(location.getSgguCode());
        } else {
            Hospital newHospital = Hospital.builder()
                    .apiId(ykiho)
                    .name(requestDto.getName())
                    .address(requestDto.getAddress())
                    .phone(phone)
                    .location(location)
                    .sidoCode(location.getSidoCode())
                    .sgguCode(location.getSgguCode())
                    .build();
            hospitalRepository.save(newHospital);
        }
    }

    @Transactional
    public void upsertHospital(HospitalResponseDto responseDto, Location location) {
        String ykiho = responseDto.getApiId();
        if (ykiho == null || ykiho.isBlank()) {
            log.warn("API ID (ykiho)가 null이거나 비어있어 처리를 건너뜁니다. DTO: name={}, address={}", responseDto.getName(), responseDto.getAddress());
            return;
        }

        log.info("[UPSERT] 병원 처리 시작. API ID: {}, 병원명: {}, 지역: {}", ykiho, responseDto.getName(), location.getName());

        try {
            Optional<Hospital> existingHospitalOpt = hospitalRepository.findByApiId(ykiho);
            String phone = (responseDto.getPhone() == null || responseDto.getPhone().isBlank()) ? "정보 없음" : responseDto.getPhone();

            if (existingHospitalOpt.isPresent()) {
                Hospital hospitalToUpdate = existingHospitalOpt.get();
                log.info("[UPSERT] 기존 병원 발견 (ID: {}). 정보 업데이트를 시도합니다. API ID: {}", hospitalToUpdate.getId(), ykiho);
                hospitalToUpdate.setName(responseDto.getName());
                hospitalToUpdate.setAddress(responseDto.getAddress());
                hospitalToUpdate.setPhone(phone);
                hospitalToUpdate.setLocation(location);
                hospitalToUpdate.setSidoCode(location.getSidoCode());
                hospitalToUpdate.setSgguCode(location.getSgguCode());
                log.info("[UPSERT] 병원 정보 업데이트 완료. API ID: {}", ykiho);

            } else {
                log.info("[UPSERT] 신규 병원입니다. 저장을 시도합니다. API ID: {}", ykiho);
                Hospital newHospital = Hospital.builder()
                        .apiId(ykiho)
                        .name(responseDto.getName())
                        .address(responseDto.getAddress())
                        .phone(phone)
                        .location(location)
                        .sidoCode(location.getSidoCode())
                        .sgguCode(location.getSgguCode())
                        .build();
                Hospital savedHospital = hospitalRepository.save(newHospital);
                log.info("[UPSERT] 신규 병원 저장 성공. 생성된 DB ID: {}, API ID: {}", savedHospital.getId(), ykiho);
            }
        } catch (Exception e) {
            log.error("[UPSERT] 병원 정보 처리 중 심각한 예외 발생! API ID: {}. 롤백될 수 있습니다.", ykiho, e);
        }
    }

    /**
     * [신규 추가] 외부 API로부터 받아온 병원 목록을 일괄 처리(저장/업데이트)하는 메서드입니다.
     * 진료과목 정보를 효율적으로 처리하기 위해 새로 추가되었습니다.
     */
    @Transactional
    public void saveHospitalDataBatch(List<HospitalResponseDto> hospitalDtos, Location location) {
        if (hospitalDtos == null || hospitalDtos.isEmpty()) {
            return;
        }

        // DB에 존재하는 모든 진료과목을 코드를 key로 하여 Map으로 미리 불러옵니다. (효율성 증대)
        Map<String, Subject> subjectMap = subjectRepository.findAll().stream()
                .collect(Collectors.toMap(Subject::getCode, subject -> subject));

        for (HospitalResponseDto dto : hospitalDtos) {
            String ykiho = dto.getApiId();
            if (!StringUtils.hasText(ykiho)) {
                log.warn("API ID (ykiho)가 없어 처리를 건너뜁니다: {}", dto.getName());
                continue;
            }

            // DTO의 진료과목 코드(예: "01,02,05")를 파싱하여 Subject 엔티티 Set으로 변환합니다.
            Set<Subject> subjects = new HashSet<>();
            String subjectCodes = dto.getSubjectCodes();
            if (StringUtils.hasText(subjectCodes)) {
                String[] codes = subjectCodes.split(",");
                for (String code : codes) {
                    Subject subject = subjectMap.get(code.trim());
                    if (subject != null) {
                        subjects.add(subject);
                    }
                }
            }

            // DB에서 병원 정보를 찾아 업데이트하거나, 없으면 새로 생성합니다.
            Optional<Hospital> existingHospitalOpt = hospitalRepository.findByApiId(ykiho);
            Hospital hospital = existingHospitalOpt.orElseGet(() -> Hospital.builder().apiId(ykiho).build());

            hospital.setName(dto.getName());
            hospital.setAddress(dto.getAddress());
            hospital.setPhone((dto.getPhone() == null || dto.getPhone().isBlank()) ? "정보 없음" : dto.getPhone());
            hospital.setLocation(location);
            hospital.setSidoCode(location.getSidoCode());
            hospital.setSgguCode(location.getSgguCode());
            hospital.setSubjects(subjects);

            hospitalRepository.save(hospital);
        }
    }
}