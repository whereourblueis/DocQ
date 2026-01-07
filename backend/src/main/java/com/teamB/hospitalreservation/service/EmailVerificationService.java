package com.teamB.hospitalreservation.service;

import com.teamB.hospitalreservation.entity.EmailVerification;
import com.teamB.hospitalreservation.repository.EmailVerificationRepository;
import com.teamB.hospitalreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;

    /**
     * 이메일이 이미 등록(회원가입)되어 있지 않은 경우에만 인증코드 발송
     */
    @Transactional
    public void sendVerificationCode(String email) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        String code = String.format("%06d", new Random().nextInt(1000000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmail(email);
        emailVerification.setCode(code);
        emailVerification.setExpiry(expiry);
        emailVerification.setVerified(false);
        emailVerificationRepository.save(emailVerification);

        String subject = "[병원예약] 이메일 인증 코드 안내";

        String html = loadHtmlTemplateWithCode("templates/email_verification.html", code);

        sendHtmlMail(email, subject, html);
    }

    private String loadHtmlTemplateWithCode(String templatePath, String code) {
        try {
            ClassPathResource resource = new ClassPathResource(templatePath);
            String html;
            try (InputStream is = resource.getInputStream()) {
                html = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
            return html.replace("{{CODE}}", code);
        } catch (Exception e) {
            throw new RuntimeException("이메일 템플릿 읽기 오류", e);
        }
    }

    private void sendHtmlMail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true); // HTML 형식
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    @Transactional
    public boolean verifyCode(String email, String code) {
        Optional<EmailVerification> verificationOpt =
                emailVerificationRepository.findByEmailAndCodeAndVerifiedFalse(email, code);
        if (verificationOpt.isEmpty()) return false;

        EmailVerification verification = verificationOpt.get();
        if (verification.getExpiry().isBefore(LocalDateTime.now())) return false;

        verification.setVerified(true);
        emailVerificationRepository.save(verification);
        return true;
    }
}