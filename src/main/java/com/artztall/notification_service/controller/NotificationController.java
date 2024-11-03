package com.artztall.notification_service.controller;

import com.artztall.notification_service.dto.NotificationRequest;
import com.artztall.notification_service.dto.NotificationResponse;
import com.artztall.notification_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "APIs for managing user notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "Send a new notification",
            description = "Creates and sends a new notification to specified user(s)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid notification request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(
            @Parameter(description = "Notification details", required = true)
            @RequestBody NotificationRequest request) {
        notificationService.sendNotification(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get all notifications for a user",
            description = "Retrieves all notifications (read and unread) for the specified user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notifications retrieved successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = NotificationResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @Operation(
            summary = "Get unread notifications for a user",
            description = "Retrieves only unread notifications for the specified user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Unread notifications retrieved successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = NotificationResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @Operation(
            summary = "Mark notification as read",
            description = "Updates the status of a notification to 'read'"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification marked as read successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "ID of the notification to mark as read", required = true)
            @PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}