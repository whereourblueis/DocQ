package com.teamB.hospitalreservation.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {

        System.err.println("Error: " + ex.getMessage());

        return ResponseEntity.status(500).body("예기치 않은 오류가 발생했습니다. 나중에 다시 시도해 주세요.");
    }
}