package com.utown.service;

import com.utown.exception.UnauthorizedException;
import com.utown.model.entity.RefreshToken;
import com.utown.model.entity.User;
import com.utown.repository.RefreshTokenRepository;
import com.utown.security.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public RefreshToken createRefreshToken(User user, String tokenString) {
        refreshTokenRepository.deleteByUserId(user.getId());

        long ttlMillis = jwtTokenProvider.getRefreshTokenExpiration();

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(ttlMillis / 1000);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenString)
                .expiresAt(expiresAt)
                .isValid(true)
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);

        log.info("Created refresh token for user ID: {}, expires at: {}",
                user.getId(), expiresAt);

        return refreshToken;
    }

    public RefreshToken validateRefreshToken(String tokenString) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found"));

        if (refreshToken.isExpired()) {
            log.warn("Refresh token expired for user ID: {}", refreshToken.getUser().getId());
            throw new UnauthorizedException("Refresh token has expired");
        }

        if (!refreshToken.getIsValid()) {
            log.warn("Refresh token is invalid for user ID: {}", refreshToken.getUser().getId());
            throw new UnauthorizedException("Refresh token is invalid");
        }

        return refreshToken;
    }

    @Transactional
    public void invalidateRefreshToken(String tokenString) {
        refreshTokenRepository.findByToken(tokenString).ifPresent(token -> {
            token.setIsValid(false);
            refreshTokenRepository.save(token);
            log.info("Invalidated refresh token for user ID: {}", token.getUser().getId());
        });
    }

    @Transactional
    public void deleteAllUserTokens(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("Deleted all refresh tokens for user ID: {}", userId);
    }
}
