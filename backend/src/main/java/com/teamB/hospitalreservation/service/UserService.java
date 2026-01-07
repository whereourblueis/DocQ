package com.teamB.hospitalreservation.service;

import com.teamB.hospitalreservation.config.UserPrincipal;
import com.teamB.hospitalreservation.dto.SignupRequest;
import com.teamB.hospitalreservation.entity.User;
import com.teamB.hospitalreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return UserPrincipal.create(user);
    }

    private String maskRrn(String rrn) {
        if (rrn == null || rrn.length() < 7) {
            return rrn;
        }
        return rrn.substring(0, 8) + "******";
    }

    // [일반 회원가입]
    public User registerUser(SignupRequest request) {
        String maskedRrn = maskRrn(request.getRrn());
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setName(request.getName());
        user.setRrn(maskedRrn);
        user.setAddress(request.getAddress());
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setEmail(request.getEmail());
        user.setPhone_number(request.getPhone_number());
        user.setProvider(null);         // 일반 회원가입
        user.setProviderId(null);

        return userRepository.save(user);
    }

    // [소셜 회원가입/로그인 처리]
    public User registerOrGetSocialUser(String provider, String providerId, String email, String nickname) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    User user = new User();
                    user.setProvider(provider);
                    user.setProviderId(providerId);
                    user.setEmail(email);
                    user.setUsername(email); 
                    user.setName(nickname);
                    user.setPassword(null);
                    log.info("소셜 신규가입: provider={}, providerId={}, email={}", provider, providerId, email);
                    return userRepository.save(user);
                });
    }

    public User findByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}