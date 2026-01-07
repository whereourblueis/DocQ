package com.teamB.hospitalreservation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter @Setter
@RequiredArgsConstructor
public class LoginHistory {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private Date loginTime;
    private boolean success;
    private String message;
}