package com.teamB.hospitalreservation.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
LoginController
소셜 로그인 성공/실패 시 간단한 메시지를 반환하는 보조적인 컨트롤러입니다.
 */
@RestController
public class LoginController {

    @GetMapping("api/loginSuccess")
    public String loginSuccess(OAuth2AuthenticationToken authentication) {
        String provider = authentication.getAuthorizedClientRegistrationId(); // "google" or "kakao"
        String name = "";

        if (provider.equals("kakao")) {
            Map<String, Object> kakaoAccount = authentication.getPrincipal().getAttribute("kakao_account");
            if (kakaoAccount != null) {
                Object profileObj = kakaoAccount.get("profile");
                if (profileObj instanceof Map<?, ?>) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> profile = (Map<String, Object>) profileObj;
                    name = (String) profile.get("nickname");
                }
            }
        } else {
            name = authentication.getPrincipal().getAttribute("name");
        }

        return provider + " 로그인 성공 " + name + "님.";
    }

    @GetMapping("api/loginFailure")
    public String loginFailure() {
        return "로그인 실패";
    }
}



