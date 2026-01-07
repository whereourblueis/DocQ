package com.teamB.hospitalreservation.controller;

import com.teamB.hospitalreservation.config.JwtUtil;
import com.teamB.hospitalreservation.dto.LoginRequest;
import com.teamB.hospitalreservation.dto.LoginResponse;
import com.teamB.hospitalreservation.dto.SignupRequest;
import com.teamB.hospitalreservation.entity.User;
import com.teamB.hospitalreservation.entity.LoginHistory;
import com.teamB.hospitalreservation.repository.UserRepository;
import com.teamB.hospitalreservation.repository.LoginHistoryRepository;
import com.teamB.hospitalreservation.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.*;

/*
AuthController
사용자의 회원가입, 로그인, 인증 처리를 총괄하는 컨트롤러입니다. 이 시스템의 인증(Authentication)관문 역할을 합니다.

POST /api/auth/join: 신규 사용자 등록 (회원가입)
POST /api/auth/login: 아이디와 비밀번호를 사용한 일반 로그인 처리 및 JWT 토큰 발급
GET /api/auth/oauth2/success: 구글, 카카오 소셜 로그인 성공 후 호출되는 콜백을 처리. 소셜 서비스로부터 사용자 정보를 받아
                              앱의 JWT 토큰을 발급하고, 프론트엔드로 리다이렉트합니다.
GET /api/auth/user/{username}: 특정 사용자의 기본 정보(이름, 이메일 등)를 조회합니다.
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            userService.registerUser(request);
            return ResponseEntity.ok(Map.of("success", true, "message", "회원가입 성공!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 일반 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);

        boolean success = false;
        String message = "";
        if (user == null) {
            message = "사용자를 찾을 수 없습니다.";
        } else if (user.getPassword() == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            message = "비밀번호가 일치하지 않습니다.";
        } else {
            success = true;
            message = "로그인 성공!";
        }

        LoginHistory history = new LoginHistory();
        history.setUsername(loginRequest.getUsername());
        history.setLoginTime(new Date());
        history.setSuccess(success);
        history.setMessage(message);
        loginHistoryRepository.save(history);

        if (!success) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", message));
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(Map.of("success", true, "message", message, "token", token));
    }

    // 소셜 로그인 성공 콜백
    @GetMapping("/oauth2/success")
    public ResponseEntity<?> socialLoginSuccess(OAuth2AuthenticationToken authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보를 찾을 수 없습니다.");
        }

        OAuth2User oAuth2User = authentication.getPrincipal();
        String registrationId = authentication.getAuthorizedClientRegistrationId();
        String email = null;

        if ("google".equals(registrationId)) {
            email = oAuth2User.getAttribute("email");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");
            }
        }

        if (email == null) {
            return ResponseEntity.badRequest().body("이메일 정보를 가져올 수 없습니다. 이메일 제공에 동의해주세요.");
        }

        final String finalEmail = email;

        User user = userRepository.findByEmail(finalEmail)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다: " + finalEmail));

        String token = jwtUtil.generateToken(user.getUsername());

        LoginHistory history = new LoginHistory();
        history.setUsername(user.getUsername());
        history.setLoginTime(new Date());
        history.setSuccess(true);
        history.setMessage("소셜 로그인 성공 (" + registrationId + ")");
        loginHistoryRepository.save(history);

        String redirectUrl = "http://localhost:5173/oauth2/redirect?token=" + token + "&email=" + finalEmail;

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "유저 정보 없음"));
        }
        User user = userOpt.get();
        Map<String, Object> info = new HashMap<>();
        info.put("username", user.getUsername());
        info.put("name", user.getName());
        info.put("email", user.getEmail());
        return ResponseEntity.ok(Map.of("success", true, "user", info));
    }
}