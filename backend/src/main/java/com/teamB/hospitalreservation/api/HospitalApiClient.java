package com.teamB.hospitalreservation.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamB.hospitalreservation.dto.HospitalResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/*
HospitalApiClient
외부 Api를 호출하여 병원 정보를 가져오는 클라이언트

RestTemplate을 사용하여 지정된 URL로 HTTP 요청을 보냅니다.
시/도, 시/군/구, 병원 이름 등의 조건으로 병원 데이터를 요청하고, 페이징 처리를 통해
모든 결과를 가져옵니다.

Api 서버로부터 받은 JSON 형식의 응답을 파싱하여
HospitalResponce 객체 리스트로 변환합니다.
 */
@Slf4j
@Component
public class HospitalApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.key}")
    private String API_KEY;

    private static final String HOSPITAL_API_PATH = "/getHospBasisList";
    private static final String BASE_URL = "https://apis.data.go.kr/B551182/hospInfoServicev2";
    private static final int NUM_OF_ROWS = 100;

    public HospitalApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<HospitalResponseDto> callApi(String sidoCd, String sgguCd, String name) {
        List<HospitalResponseDto> allHospitals = new ArrayList<>();
        int pageNo = 1;

        try {
            String encodedApiKey = URLEncoder.encode(API_KEY, StandardCharsets.UTF_8);

            while (true) {
                String url = String.format("%s%s?serviceKey=%s&pageNo=%d&numOfRows=%d&_type=json",
                        BASE_URL, HOSPITAL_API_PATH, encodedApiKey, pageNo, NUM_OF_ROWS);

                if (sidoCd != null && !sidoCd.isBlank()) {
                    url += "&sidoCd=" + URLEncoder.encode(sidoCd, StandardCharsets.UTF_8);
                }
                if (sgguCd != null && !sgguCd.isBlank()) {
                    url += "&sgguCd=" + URLEncoder.encode(sgguCd, StandardCharsets.UTF_8);
                }

                if (name != null && !name.isBlank()) {
                    url += "&yadmNm=" + URLEncoder.encode(name, StandardCharsets.UTF_8);
                }

                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
                String jsonString = response.getBody();

                if (jsonString != null && jsonString.trim().startsWith("<")) {
                    handleXmlError(jsonString);
                    break;
                }

                ParsedResult parsedResult = parseJson(jsonString);
                if (parsedResult.getHospitals() != null) {
                    allHospitals.addAll(parsedResult.getHospitals());
                }

                if (allHospitals.size() >= parsedResult.getTotalCount() || parsedResult.getHospitals() == null || parsedResult.getHospitals().isEmpty()) {
                    break;
                }
                pageNo++;
            }
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생", e);
        }
        return allHospitals;
    }

    private void handleXmlError(String xmlResponse) {
        if (xmlResponse.contains("<resultCode>01</resultCode>")) {
            log.warn("API 호출 한도를 초과했습니다.");
        } else {
            log.error("API가 XML 형식의 오류를 반환했습니다: {}", xmlResponse);
        }
    }

    @Getter
    @AllArgsConstructor
    private static class ParsedResult {
        private final List<HospitalResponseDto> hospitals;
        private final int totalCount;
    }

    private ParsedResult parseJson(String jsonString) throws Exception {
        JsonNode root = objectMapper.readTree(jsonString);
        JsonNode itemsNode = root.path("response").path("body").path("items").path("item");
        int totalCount = root.path("response").path("body").path("totalCount").asInt();

        List<HospitalResponseDto> hospitals;
        if (itemsNode.isMissingNode() || itemsNode.isNull()) {
            return new ParsedResult(Collections.emptyList(), 0);
        }

        if (itemsNode.isArray()) {
            hospitals = objectMapper.convertValue(itemsNode, new TypeReference<List<HospitalResponseDto>>() {
            });
        } else {
            HospitalResponseDto hospital = objectMapper.convertValue(itemsNode, HospitalResponseDto.class);
            hospitals = Collections.singletonList(hospital);
        }

        if (hospitals != null && !hospitals.isEmpty()) {
            HospitalResponseDto firstHospital = hospitals.get(0);
            log.info("API 파싱 결과: 첫 번째 병원 DTO의 apiId(ykiho)='{}', name(yadmNm)='{}'. (총 {}건 파싱)",
                    firstHospital.getApiId(), firstHospital.getName(), hospitals.size());
        }
        
        return new ParsedResult(hospitals, totalCount);
    }
}