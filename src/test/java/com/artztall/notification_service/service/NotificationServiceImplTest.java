package com.artztall.notification_service.service;

import com.artztall.notification_service.dto.NotificationRequest;
import com.artztall.notification_service.dto.NotificationResponse;
import com.artztall.notification_service.model.Notification;
import com.artztall.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceImplTest {

    private NotificationServiceImpl notificationService;
    private NotificationRepository notificationRepository;
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationRepository = mock(NotificationRepository.class);
        messagingTemplate = mock(SimpMessagingTemplate.class);
        notificationService = new NotificationServiceImpl(notificationRepository, messagingTemplate);
    }

    @Test
    void testSendNotification() {
        NotificationRequest request = new NotificationRequest();
        request.setUserId("user123");
        request.setMessage("Your order #12345 has been shipped");
        request.setType("INFO");
        request.setActionUrl("/orders/12345");

        Notification savedNotification = new Notification();
        savedNotification.setId("notif-123e4567-e89b");
        savedNotification.setUserId("user123");
        savedNotification.setMessage("Your order #12345 has been shipped");
        savedNotification.setType("INFO");
        savedNotification.setRead(false);
        savedNotification.setCreatedAt(LocalDateTime.now());
        savedNotification.setActionUrl("/orders/12345");

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        notificationService.sendNotification(request);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(messagingTemplate, times(1)).convertAndSendToUser(
                eq("user123"),
                eq("/topic/notifications"),
                any(NotificationResponse.class)
        );
    }

    @Test
    void testGetUserNotifications() {
        Notification notification = new Notification();
        notification.setId("notif-123");
        notification.setUserId("user123");
        notification.setMessage("Test notification");
        notification.setType("INFO");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setActionUrl("/test-url");

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc("user123"))
                .thenReturn(Arrays.asList(notification));

        List<NotificationResponse> responseList = notificationService.getUserNotifications("user123");

        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        assertEquals("Test notification", responseList.get(0).getMessage());
    }

    @Test
    void testMarkAsRead() {
        Notification notification = new Notification();
        notification.setId("notif-123");
        notification.setUserId("user123");
        notification.setMessage("Test notification");
        notification.setType("INFO");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setActionUrl("/test-url");

        when(notificationRepository.findById("notif-123")).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);

        notificationService.markAsRead("notif-123");

        verify(notificationRepository, times(1)).save(notification);
        assertTrue(notification.isRead());
    }

    @Test
    void testGetUnreadNotifications() {
        Notification notification1 = new Notification();
        notification1.setId("notif-123");
        notification1.setUserId("user123");
        notification1.setMessage("Unread notification 1");
        notification1.setType("WARNING");
        notification1.setRead(false);
        notification1.setCreatedAt(LocalDateTime.now());
        notification1.setActionUrl("/test-url-1");

        Notification notification2 = new Notification();
        notification2.setId("notif-456");
        notification2.setUserId("user123");
        notification2.setMessage("Unread notification 2");
        notification2.setType("INFO");
        notification2.setRead(false);
        notification2.setCreatedAt(LocalDateTime.now().minusDays(1));
        notification2.setActionUrl("/test-url-2");

        when(notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc("user123", false))
                .thenReturn(Arrays.asList(notification1, notification2));

        List<NotificationResponse> responseList = notificationService.getUnreadNotifications("user123");

        assertNotNull(responseList);
        assertEquals(2, responseList.size());
    }
}
