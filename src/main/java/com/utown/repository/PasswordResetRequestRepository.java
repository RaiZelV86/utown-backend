package com.utown.repository;

import com.utown.model.entity.PasswordResetRequest;
import com.utown.model.enums.ResetPasswordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRequestRepository  extends JpaRepository<PasswordResetRequest,Long> {
    Optional<PasswordResetRequest> findByUserIdAndStatus(Long userId, ResetPasswordStatus status);
    Optional<PasswordResetRequest> findByResetToken(String resetToken);
    void deleteByUserId(Long userId);
}
