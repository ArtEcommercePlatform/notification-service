package com.artztall.notification_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(
        name = "NotificationResponse",
        description = "Response object containing notification details"
)
public class NotificationResponse {
    @Schema(
            description = "Unique identifier of the notification",
            example = "notif-123e4567-e89b",
            required = true
    )
    private String id;

    @Schema(
            description = "Content of the notification message",
            example = "Your order #12345 has been shipped",
            required = true
    )
    private String message;

    @Schema(
            description = "Type of notification (e.g., INFO, WARNING, ERROR)",
            example = "INFO",
            required = true
    )
    private String type;

    @Schema(
            description = "Indicates whether the notification has been read",
            example = "false",
            required = true
    )
    private boolean read;

    @Schema(
            description = "Timestamp when the notification was created",
            example = "2024-03-11T10:15:30",
            required = true
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "URL that will be accessed when the notification is clicked",
            example = "/orders/12345",
            required = false
    )
    private String actionUrl;
}