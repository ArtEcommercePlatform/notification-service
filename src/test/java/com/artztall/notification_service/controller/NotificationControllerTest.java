package com.artztall.notification_service.controller;

import com.artztall.notification_service.dto.NotificationRequest;
import com.artztall.notification_service.dto.NotificationResponse;
import com.artztall.notification_service.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationRequest notificationRequest;
    private NotificationResponse notificationResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setting up a mock notification request
        notificationRequest = new NotificationRequest();
        notificationRequest.setUserId("user123");
        notificationRequest.setMessage("Your order #12345 has been shipped");
        notificationRequest.setType("INFO");
        notificationRequest.setActionUrl("/orders/12345");

        // Setting up a mock notification response
        notificationResponse = new NotificationResponse();
        notificationResponse.setId("notif-123");

        notificationResponse.setMessage("Your order #12345 has been shipped");
        notificationResponse.setType("INFO");
        notificationResponse.setRead(false);
        notificationResponse.setCreatedAt(LocalDateTime.now());
        notificationResponse.setActionUrl("/orders/12345");
    }

    @Test
    void sendNotification_shouldReturnOk() throws Exception {
        // Arrange
        doNothing().when(notificationService).sendNotification(any(NotificationRequest.class));

        // Act & Assert
        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getUserNotifications_shouldReturnNotifications() throws Exception {
        // Arrange
        List<NotificationResponse> notifications = Collections.singletonList(notificationResponse);
        when(notificationService.getUserNotifications("user123")).thenReturn(notifications);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/user/{userId}", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("notif-123"))
                .andExpect(jsonPath("$[0].message").value("Your order #12345 has been shipped"));
    }

    @Test
    void getUnreadNotifications_shouldReturnUnreadNotifications() throws Exception {
        // Arrange
        List<NotificationResponse> unreadNotifications = Collections.singletonList(notificationResponse);
        when(notificationService.getUnreadNotifications("user123")).thenReturn(unreadNotifications);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/user/{userId}/unread", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("notif-123"))
                .andExpect(jsonPath("$[0].read").value(false));
    }

    @Test
    void markAsRead_shouldReturnOk() throws Exception {
        // Arrange
        doNothing().when(notificationService).markAsRead("notif-123");

        // Act & Assert
        mockMvc.perform(put("/api/notifications/{notificationId}/read", "notif-123"))
                .andExpect(status().isOk());
    }
}
