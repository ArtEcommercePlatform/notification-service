package com.artztall.notification_service.service;

import com.artztall.notification_service.dto.NotificationRequest;
import com.artztall.notification_service.dto.NotificationResponse;
import com.artztall.notification_service.model.Notification;
import com.artztall.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private NotificationRequest notificationRequest;
    private Notification notification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setting up a mock notification request
        notificationRequest = new NotificationRequest();
        notificationRequest.setUserId("user1");
        notificationRequest.setMessage("Test message");
        notificationRequest.setType("INFO");
        notificationRequest.setActionUrl("http://example.com");

        // Setting up a mock notification
        notification = new Notification();
        notification.setId("1");
        notification.setUserId("user1");
        notification.setMessage("Test message");
        notification.setType("INFO");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setActionUrl("http://example.com");
    }

    @Test
    void sendNotification_shouldSendMessage() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        notificationService.sendNotification(notificationRequest);

        // Assert
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(messagingTemplate, times(1)).convertAndSendToUser(
                eq("user1"),
                eq("/topic/notifications"),
                any(NotificationResponse.class)
        );
    }

    @Test
    void getUserNotifications_shouldReturnList() {
        // Arrange
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc("user1"))
                .thenReturn(Arrays.asList(notification));

        // Act
        List<NotificationResponse> notifications = notificationService.getUserNotifications("user1");

        // Assert
        verify(notificationRepository, times(1)).findByUserIdOrderByCreatedAtDesc("user1");
        assert(notifications.size() == 1);

    }

    @Test
    void markAsRead_shouldUpdateNotification() {
        // Arrange
        when(notificationRepository.findById("1")).thenReturn(Optional.of(notification));

        // Act
        notificationService.markAsRead("1");

        // Assert
        verify(notificationRepository, times(1)).save(notification);
        assert(notification.isRead());
    }

    @Test
    void getUnreadNotifications_shouldReturnUnreadNotifications() {
        // Arrange
        when(notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc("user1", false))
                .thenReturn(Arrays.asList(notification));

        // Act
        List<NotificationResponse> unreadNotifications = notificationService.getUnreadNotifications("user1");

        // Assert
        verify(notificationRepository, times(1)).findByUserIdAndReadOrderByCreatedAtDesc("user1", false);
        assert(unreadNotifications.size() == 1);
        assert(!unreadNotifications.get(0).isRead());
    }
}
