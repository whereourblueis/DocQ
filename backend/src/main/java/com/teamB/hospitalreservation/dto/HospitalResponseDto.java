package com.teamB.hospitalreservation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teamB.hospitalreservation.entity.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HospitalResponseDto {

    private Long id;

    @JsonProperty("yadmNm")
    private String name;

    @JsonProperty("addr")
    private String address;

    @JsonProperty("telno")
    private String phone;

    @JsonProperty("ykiho")
    private String apiId;

    @JsonProperty("dgsbjtCd")
    private String subjectCodes;

    private String subjectNames;

    private transient Location location;

    // 외부 API 응답을 파싱할 때 주로 사용될 생성자
    public HospitalResponseDto(String name, String address, String phone, String apiId, String subjectCodes) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.apiId = apiId;
        this.subjectCodes = subjectCodes;
    }

    // [추가] 서비스 레이어에서 DB 조회 결과를 DTO로 변환할 때 사용할 생성자
    public HospitalResponseDto(Long id, String name, String address, String phone, String apiId, String subjectCodes, String subjectNames) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.apiId = apiId;
        this.subjectCodes = subjectCodes;
        this.subjectNames = subjectNames;
    }

    public String getGeneratedId() {
        return this.apiId + ":" + this.name;
    }
}