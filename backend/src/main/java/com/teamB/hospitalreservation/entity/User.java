package com.teamB.hospitalreservation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(nullable = true)
    private String rrn; // 주민등록번호
    
    @Column(nullable = true)
    private String address;
    
    @Column(unique = true, nullable = true)
    private String username; // 로그인 ID
    
    @Column(nullable = true)
    private String password;
    
    @Column(unique = true)
    private String email;
    
    @Column(nullable = true)
    private String phone_number;

    // OAuth2
    private String provider;    // ex) google, kakao
    private String providerId;

    public User(String name, String email, String provider, String providerId) {
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
    }
}