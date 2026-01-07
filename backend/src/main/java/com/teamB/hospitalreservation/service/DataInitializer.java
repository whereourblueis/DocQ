package com.teamB.hospitalreservation.service;

import com.teamB.hospitalreservation.entity.Subject;
import com.teamB.hospitalreservation.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final SubjectRepository subjectRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (subjectRepository.count() == 0) {
            log.info("Subject 데이터베이스가 비어있어 초기 데이터를 생성합니다.");
            
            List<String> subjectNames = List.of(
                "내과", "피부과", "정형외과", "산부인과", "치과",
                "이비인후과", "안과", "신경외과", "소아청소년과", "정신의학과", "외과"
            );

            List<Subject> subjects = IntStream.range(0, subjectNames.size())
                    .mapToObj(i -> {
                        String code = String.format("%02d", i + 1);
                        String name = subjectNames.get(i);
                        return new Subject(code, name);
                    })
                    .collect(Collectors.toList());

            subjectRepository.saveAll(subjects);
            log.info("{}개의 Subject 초기 데이터 생성을 완료했습니다.", subjects.size());
        } else {
            log.info("Subject 데이터가 이미 존재하므로 초기화를 건너뜁니다.");
        }
    }
}