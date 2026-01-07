package com.teamB.hospitalreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDto {
    private Long id;
    private String code;
    private String name;
}