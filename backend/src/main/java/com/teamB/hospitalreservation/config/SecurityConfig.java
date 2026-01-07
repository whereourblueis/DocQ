package com.teamB.hospitalreservation.config;

import com.teamB.hospitalreservation.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

/*
전반적인 보안 설정을 총괄하는 클래스
CORS 설정 : http://localhost:5173 (프론트엔드 개발 서버) 에서의 요청을 허용하도록 설정
두 개의 보안 필터 체인:
1. 소셜 로그인 전용 설정 (oauth2LoginFilterChain)
=> 소셜 로그인 관련 경로 (/login/oauth2/** 등)에 대한 보안을 처리. 소셜 로그인은 세션을 기반으로 동작합니다

2. API 전용 설정 (apiFilterChain)
=> /api/** 경로의 요청에 대한 보안을 처리합니다.
 */



@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtRequestFilter jwtRequestFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    // CORS 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedOriginPatterns(Collections.singletonList("http://localhost:5173"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * 1. 소셜 로그인 및 성공 후 처리 전용 시큐리티 설정 (세션 사용)
     */
    @Bean
    @Order(1)
    public SecurityFilterChain oauth2LoginFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/login/oauth2/**", "/oauth2/**", "/api/auth/oauth2/success")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .defaultSuccessUrl("/api/auth/oauth2/success", true)
                );
        return http.build();
    }

    /**
     * 2. API 및 기타 요청용 시큐리티 설정 (Stateless)
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // 이 필터 체인은 /api/** 경로에만 적용
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler.authenticationEntryPoint(restAuthenticationEntryPoint))

                .authorizeHttpRequests(auth -> auth
                        // 병원 검색(GET), 데이터 초기화(POST), 인증(로그인/가입) 관련 경로만 허용합니다.
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/hospitals/search", "/api/reservations/booked-times").permitAll()
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/hospital-data/init",
                                "/api/email/**",
                                "api/locations/init",
                                "api/hospital-data/init"
                        ).permitAll()
                        // 그 외의 모든 요청은 인증이 필요합니다.
                        .anyRequest().authenticated())

                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}