package com.teamB.hospitalreservation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamB.hospitalreservation.entity.Subject;
import com.teamB.hospitalreservation.repository.SubjectRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api/subject-db")

public class SubjectDbController {

    private final SubjectRepository subjectRepository;

    @GetMapping("/api/subject-db")

    public List<Subject> getAllSubjects(){
        return subjectRepository.findAll();
    }

}
