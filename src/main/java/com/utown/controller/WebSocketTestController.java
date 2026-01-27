package com.utown.controller;

import com.utown.model.dto.notification.NotificationDTO;
import com.utown.model.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebSocketTestController {

    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/api/test/notification/user/{userId}")
    public String sendTestNotificationToUser(@PathVariable Long userId) {
        NotificationDTO notification = NotificationDTO.builder()
                .type(NotificationType.ORDER_CREATED)
                .title("Test Notification")
                .message("This is a test notification for user " + userId)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
        );

        log.info("Test notification sent to user {}", userId);
        return "Test notification sent to user " + userId;
    }

    @GetMapping("/api/test/notification/broadcast")
    public String sendTestBroadcast() {
        NotificationDTO notification = NotificationDTO.builder()
                .type(NotificationType.ORDER_CREATED)
                .title("Broadcast Test")
                .message("This is a broadcast test notification")
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/notifications", notification);

        log.info("Test broadcast sent");
        return "Test broadcast sent to all connected clients";
    }
}
