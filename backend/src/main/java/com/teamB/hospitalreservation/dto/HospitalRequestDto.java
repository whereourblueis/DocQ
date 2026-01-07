package com.teamB.hospitalreservation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HospitalRequestDto {
    private String apiId; // ykiho 값을 받을 필드
    private String name;
    private String address;
    private String phone;
    private String sidoCode;
    private String sgguCode;
    private String subjectCode;
}