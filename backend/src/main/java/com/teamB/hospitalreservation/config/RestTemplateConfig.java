package com.teamB.hospitalreservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.nio.charset.StandardCharsets;

/*
RestTemplateConfig 클래스는 안정적이고 일관된 외부 API 연동을 위해 RestTemplate 객체를 다음과 같이 설정하여 제공
* 안정성: 적절한 타임아웃 설정으로 외부 서비스 장애가 우리 시스템 전체에 영향을 미치는 것을 최소화
* 정확성: URI 자동 인코딩을 방지하여 인증키 등이 포함된 요청을 올바르게 보낼 수 있음
* 호환성: 응답 데이터의 한글 깨짐을 방지하기 위해 UTF-8 인코딩을 기본으로 사용합니다.
 */


@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000); // 연결 시도 시간
        requestFactory.setReadTimeout(30000);   // 응답 대기 시간
        restTemplate.setRequestFactory(requestFactory);

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        restTemplate.setUriTemplateHandler(factory);

        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        return restTemplate;
    }
}