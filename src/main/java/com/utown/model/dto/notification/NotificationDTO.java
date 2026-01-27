package com.utown.model.dto.notification;

import com.utown.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private NotificationType type;
    private String title;
    private String message;
    private Object data;
    private LocalDateTime timestamp;
}
