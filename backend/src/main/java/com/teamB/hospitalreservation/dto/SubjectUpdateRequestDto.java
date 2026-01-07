package com.teamB.hospitalreservation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubjectUpdateRequestDto {
    private List<String> subjectNames;
}