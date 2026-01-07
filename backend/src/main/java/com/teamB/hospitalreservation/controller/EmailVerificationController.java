package com.teamB.hospitalreservation.controller;

import com.teamB.hospitalreservation.dto.EmailSendRequest;
import com.teamB.hospitalreservation.dto.VerifyRequestDto;
import com.teamB.hospitalreservation.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/*
EmailVerificationController
회원가입 시 이메일 소유권을 확인하기 위한 인증 절차를 담당합니다.
POST /api/email/send: 입력된 이메일 주소로 6자리 인증 코드를 발송합니다.
POST /api/email/verify: 사용자에게 전달된 인증 코드가 유효한지 검증합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    // 1. 인증코드 발송 요청 (기존 코드와 동일)
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendCode(@RequestBody EmailSendRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            emailVerificationService.sendVerificationCode(request.getEmail());
            result.put("success", true);
            result.put("message", "인증코드를 이메일로 전송했습니다.");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 2. 인증코드 확인 (수정된 부분)
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody VerifyRequestDto request) { // @RequestParam 대신 @RequestBody 사용
        Map<String, Object> result = new HashMap<>();
        // DTO에서 email과 code를 가져와서 사용
        boolean success = emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        result.put("success", success);
        result.put("message", success ? "이메일 인증 성공!" : "인증 실패 또는 코드 만료.");
        if (success) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}