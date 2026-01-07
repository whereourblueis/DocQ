package com.teamB.hospitalreservation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyRequestDto {
    private String email;
    private String code;
}