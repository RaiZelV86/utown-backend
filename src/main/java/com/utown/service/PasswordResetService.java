package com.utown.service;

import com.utown.exception.BadRequestException;
import com.utown.exception.NotFoundException;
import com.utown.model.dto.auth.PasswordResetConfirmDto;
import com.utown.model.dto.auth.PasswordResetRequestDto;
import com.utown.model.dto.auth.PasswordResetVerifyDto;
import com.utown.model.entity.PasswordResetRequest;
import com.utown.model.entity.User;
import com.utown.model.enums.ResetPasswordStatus;
import com.utown.repository.PasswordResetRequestRepository;
import com.utown.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetRequestRepository passwordResetRequestRepository;

    private static final String HARDCODED_CODE = "1234";
    private static final int CODE_EXPIRATION_MINUTES = 15;
    private static final int MAX_ATTEMPTS = 3;

    @Transactional
    public Map<String, Object> requestPasswordReset(PasswordResetRequestDto request) {
        log.info("Password reset requested for username: {}", request.getUsername());

        User user = userRepository.findByPhoneNumber(request.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        passwordResetRequestRepository.deleteByUserId(user.getId());

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);
        PasswordResetRequest resetRequest = PasswordResetRequest.builder()
                .user(user)
                .code(HARDCODED_CODE)  // В продакшене: generateRandomCode()
                .expiresAt(expiresAt)
                .attempts(0)
                .status(ResetPasswordStatus.PENDING)
                .build();

        passwordResetRequestRepository.save(resetRequest);

        log.info("Password reset code issued for userId: {}", user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Reset code issued");
        response.put("expiresInSeconds", CODE_EXPIRATION_MINUTES * 60);

        response.put("code", HARDCODED_CODE);

        return response;
    }

    @Transactional
    public Map<String, Object> verifyResetCode(PasswordResetVerifyDto request) {
        log.info("Verifying reset code for username: {}", request.getUsername());

        User user = userRepository.findByPhoneNumber(request.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        PasswordResetRequest resetRequest = passwordResetRequestRepository
                .findByUserIdAndStatus(user.getId(), ResetPasswordStatus.PENDING)
                .orElseThrow(() -> new BadRequestException("No pending reset request found"));

        if (resetRequest.isExpired()) {
            resetRequest.setStatus(ResetPasswordStatus.EXPIRED);
            passwordResetRequestRepository.save(resetRequest);
            throw new BadRequestException("Reset code has expired");
        }

        if (resetRequest.hasExceededAttempts()) {
            resetRequest.setStatus(ResetPasswordStatus.INVALIDATED);
            passwordResetRequestRepository.save(resetRequest);
            throw new BadRequestException("Too many failed attempts");
        }

        if (!resetRequest.getCode().equals(request.getCode())) {
            resetRequest.setAttempts(resetRequest.getAttempts() + 1);
            passwordResetRequestRepository.save(resetRequest);

            int remainingAttempts = MAX_ATTEMPTS - resetRequest.getAttempts();
            throw new BadRequestException(
                    "Invalid code. Remaining attempts: " + remainingAttempts
            );
        }

        String resetToken = UUID.randomUUID().toString();

        resetRequest.setResetToken(resetToken);
        resetRequest.setStatus(ResetPasswordStatus.VERIFIED);
        resetRequest.setVerifiedAt(LocalDateTime.now());
        resetRequest.setExpiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES));

        passwordResetRequestRepository.save(resetRequest);

        log.info("Reset code verified for userId: {}", user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("resetToken", resetToken);
        response.put("expiresInSeconds", CODE_EXPIRATION_MINUTES * 60);

        return response;
    }

    @Transactional
    public Map<String, Object> confirmPasswordReset(PasswordResetConfirmDto request) {
        log.info("Confirming password reset with token");

        PasswordResetRequest resetRequest = passwordResetRequestRepository
                .findByResetToken(request.getResetToken())
                .orElseThrow(() -> new BadRequestException("Invalid reset token"));

        if (resetRequest.getStatus() != ResetPasswordStatus.VERIFIED) {
            throw new BadRequestException("Reset token is not verified");
        }

        if (resetRequest.isExpired()) {
            resetRequest.setStatus(ResetPasswordStatus.EXPIRED);
            passwordResetRequestRepository.save(resetRequest);
            throw new BadRequestException("Reset token has expired");
        }

        User user = resetRequest.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetRequest.setStatus(ResetPasswordStatus.COMPLETED);
        resetRequest.setCompletedAt(LocalDateTime.now());
        passwordResetRequestRepository.save(resetRequest);

        log.info("Password reset completed for userId: {}", user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password updated successfully");

        return response;
    }

    private String generateRandomCode() {
        int code = (int) (Math.random() * 10000);
        return String.format("%04d", code);
    }
}
