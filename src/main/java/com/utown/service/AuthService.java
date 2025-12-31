package com.utown.service;

import com.utown.exception.BadRequestException;
import com.utown.exception.UnauthorizedException;
import com.utown.model.dto.auth.AuthResponse;
import com.utown.model.dto.auth.LoginRequest;
import com.utown.model.dto.auth.RegisterRequest;
import com.utown.model.entity.RefreshToken;
import com.utown.model.entity.User;
import com.utown.model.enums.UserRole;
import com.utown.repository.RefreshTokenRepository;
import com.utown.repository.UserRepository;
import com.utown.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getPhoneNumber());


        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("User with this phone number already exists");
        }

        User user = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(UserRole.CLIENT)  // По умолчанию CLIENT
                .isActive(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: userId={}", user.getId());

        String accessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getRole().name()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        saveRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .userId(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .name(user.getName())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getPhoneNumber());

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is disabled");
        }

        log.info("User logged in successfully: userId={}", user.getId());

        String accessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getRole().name()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        refreshTokenRepository.deleteByUserId(user.getId());

        saveRefreshToken(user, refreshToken);

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return AuthResponse.builder()
                .userId(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .name(user.getName())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthResponse refresh(String refreshTokenString) {
        log.info("Refreshing access token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (!refreshToken.getIsValid()) {
            throw new UnauthorizedException("Refresh token has been invalidated");
        }

        if (refreshToken.isExpired()) {
            throw new UnauthorizedException("Refresh token has expired");
        }

        User user = refreshToken.getUser();

        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is disabled");
        }

        log.info("Access token refreshed for userId={}", user.getId());

        String newAccessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getRole().name()
        );

        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        refreshToken.setIsValid(false);
        refreshTokenRepository.save(refreshToken);

        saveRefreshToken(user, newRefreshToken);

        return AuthResponse.builder()
                .userId(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .name(user.getName())
                .role(user.getRole().name())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void logout(Long userId) {
        log.info("User logout: userId={}", userId);

        refreshTokenRepository.deleteByUserId(userId);

        log.info("All refresh tokens invalidated for userId={}", userId);
    }

    private void saveRefreshToken(User user, String tokenString) {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenString)
                .expiresAt(expiresAt)
                .isValid(true)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}