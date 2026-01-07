package com.teamB.hospitalreservation.service;

import com.teamB.hospitalreservation.entity.User;
import com.teamB.hospitalreservation.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        String providerId = null;
        String email = null;
        String nickname = null;

        if ("kakao".equals(registrationId)) {
            providerId = String.valueOf(attributes.get("id"));
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            
            email = (String) kakaoAccount.get("email");
            nickname = (String) profile.get("nickname");

            if (email == null) {
                email = providerId + "@kakao.temp";
                
                Map<String, Object> mutableKakaoAccount = new HashMap<>(kakaoAccount);
                mutableKakaoAccount.put("email", email);
                attributes.put("kakao_account", mutableKakaoAccount);
            }

        } else if ("google".equals(registrationId)) {
            providerId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            nickname = (String) attributes.get("name");
        }

        saveOrUpdateUser(email, nickname, registrationId, providerId);

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                userNameAttributeName);
    }

    // 사용자를 저장하거나 업데이트하는 로직 (providerId 및 email 기반)
    private User saveOrUpdateUser(String email, String name, String provider, String providerId) {
        Optional<User> userOptional = userRepository.findByProviderAndProviderId(provider, providerId);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setName(name);
            user.setEmail(email);
        } else {
            // 새로운 OAuth2 사용자인 경우, provider와 providerId를 조합하여 고유한 username을 생성합니다.
            String username = provider + "_" + providerId;
            user = User.builder()
                    .username(username)
                    .name(name)
                    .email(email)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
        }
        return userRepository.save(user);
    }
}