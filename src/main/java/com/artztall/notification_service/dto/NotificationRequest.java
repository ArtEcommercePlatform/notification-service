package com.artztall.notification_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(
        name = "NotificationRequest",
        description = "Request object for creating a new notification"
)
public class NotificationRequest {
    @Schema(
            description = "ID of the user to receive the notification",
            example = "user123",
            required = true
    )
    private String userId;

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
            description = "URL that will be accessed when the notification is clicked",
            example = "/orders/12345",
            required = false
    )
    private String actionUrl;
}
