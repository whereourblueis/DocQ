package com.teamB.hospitalreservation.controller;

import com.teamB.hospitalreservation.config.JwtUtil;
import com.teamB.hospitalreservation.dto.LoginRequest;
import com.teamB.hospitalreservation.dto.LoginResponse;
import com.teamB.hospitalreservation.dto.SignupRequest;
import com.teamB.hospitalreservation.entity.User;
import com.teamB.hospitalreservation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
UserController
API 경로(api/signup, api/login)를 통해 명시적으로 회원가입과 로그인을 처리하고, URL에 포함된 사용자 이름(username)으로
특정 사용자의 정보를 조회하는 기능을 제공합니다.
 */

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공!");
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.getUsername());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 사용자입니다.");
        }

        if (!userService.checkPassword(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    // 회원 정보 조회
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }
}