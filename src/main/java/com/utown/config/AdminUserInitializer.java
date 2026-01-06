package com.utown.config;

import com.utown.model.entity.User;
import com.utown.model.enums.UserRole;
import com.utown.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.phone:+821009999999}")
    private String adminPhone;

    @Value("${admin.default.password:Admin@123}")
    private String adminPassword;

    @Value("${admin.default.name:System Administrator}")
    private String adminName;

    @Value("${admin.auto-create:true}")
    private Boolean autoCreate;

    @Override
    public void run(String... args) {
        if (!autoCreate) {
            log.info("Admin auto-creation is disabled");
            return;
        }

        if (userRepository.existsByPhoneNumber(adminPhone)) {
            log.info("Admin user already exists: {}", adminPhone);
            return;
        }

        try {
            User admin = User.builder()
                    .phoneNumber(adminPhone)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .name(adminName)
                    .role(UserRole.ADMIN)
                    .isActive(true)
                    .emailVerified(false)
                    .build();

            userRepository.save(admin);

            log.info("===========================================");
            log.info("Admin user created successfully!");
            log.info("Phone: {}", adminPhone);
            log.info("Password: {}", adminPassword);
            log.info("IMPORTANT: Please change the default password after first login!");
            log.info("===========================================");
        } catch (Exception e) {
            log.error("Failed to create admin user: {}", e.getMessage(), e);
        }
    }
}
