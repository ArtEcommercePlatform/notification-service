package com.artztall.notification_service.service;


import com.artztall.notification_service.dto.NotificationRequest;
import com.artztall.notification_service.dto.NotificationResponse;
import com.artztall.notification_service.model.Notification;
import com.artztall.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setActionUrl(request.getActionUrl());

        notification = notificationRepository.save(notification);

        NotificationResponse response = convertToResponse(notification);
        messagingTemplate.convertAndSendToUser(
                request.getUserId(),
                "/topic/notifications",
                response
        );
    }

    @Override
    public List<NotificationResponse> getUserNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(String userId) {
        return notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc(userId, false)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse convertToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());
        response.setActionUrl(notification.getActionUrl());
        return response;
    }
}
