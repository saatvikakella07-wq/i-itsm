package com.itsm.controller;

import com.itsm.dto.NotificationResponse;
import com.itsm.exception.ResourceNotFoundException;
import com.itsm.exception.UnauthorizedException;
import com.itsm.model.Notification;
import com.itsm.model.User;
import com.itsm.repository.NotificationRepository;
import com.itsm.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller exposing in-app notifications and read-status management endpoints.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new UnauthorizedException("User is not authenticated.");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
    }

    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(Authentication auth) {
        User user = getCurrentUser(auth);
        List<Notification> list = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return ResponseEntity.ok(list.stream().map(NotificationResponse::fromEntity).toList());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + id));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You cannot mark another user's notification as read.");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok(Map.of("message", "Marked as read", "id", id));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(Authentication auth) {
        User user = getCurrentUser(auth);
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read", "count", unread.size()));
    }
}
