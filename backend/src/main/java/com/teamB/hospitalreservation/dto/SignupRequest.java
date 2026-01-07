package com.teamB.hospitalreservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String name;
    private String rrn;
    private String address;
    private String username;
    private String password;
    private String email;
    private String phone_number;
}
