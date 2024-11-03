package com.artztall.notification_service.service;

import com.artztall.notification_service.dto.NotificationRequest;
import com.artztall.notification_service.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void sendNotification(NotificationRequest request);
    List<NotificationResponse> getUserNotifications(String userId);
    void markAsRead(String notificationId);
    List<NotificationResponse> getUnreadNotifications(String userId);
}
