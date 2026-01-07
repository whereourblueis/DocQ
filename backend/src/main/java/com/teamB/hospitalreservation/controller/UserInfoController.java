package com.teamB.hospitalreservation.controller;

import com.teamB.hospitalreservation.config.UserPrincipal;
import com.teamB.hospitalreservation.dto.UserInfoResponse;
import com.teamB.hospitalreservation.entity.User;
import com.teamB.hospitalreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
UserInfoController
현재 인증된 사용자(나 자신)의 정보를 조회하는 컨트롤러입니다.
GET /api/user/info: 현재 시스템에 로그인된 사용자의 정보를 조회합니다.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserRepository userRepository;

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal Object principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "인증된 사용자 정보가 없습니다."));
        }

        String email = null;
        if (principal instanceof UserPrincipal) {
            // 1. 일반 로그인 사용자 처리
            email = ((UserPrincipal) principal).getEmail();
        } else if (principal instanceof OAuth2User) {
            // 2. 소셜 로그인 사용자 처리
            OAuth2User oAuth2User = (OAuth2User) principal;
            Map<String, Object> attributes = oAuth2User.getAttributes();

            if (attributes.containsKey("email")) {
                email = (String) attributes.get("email");
            } else if (attributes.containsKey("kakao_account")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                if (kakaoAccount != null) {
                    email = (String) kakaoAccount.get("email");
                }
            }
        }

        if (email == null) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "사용자 이메일 정보를 찾을 수 없습니다."));
        }

        final String finalEmail = email;
        User user = userRepository.findByEmail(finalEmail)
                .orElseThrow(() -> new RuntimeException("DB에서 사용자 정보를 찾을 수 없습니다: " + finalEmail));

        UserInfoResponse userInfo = new UserInfoResponse(
                user.getUsername(),
                user.getName(),
                user.getEmail()
        );

        return ResponseEntity.ok(Map.of("success", true, "user", userInfo));
    }
}