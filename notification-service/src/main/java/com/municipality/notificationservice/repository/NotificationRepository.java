package com.municipality.notificationservice.repository;

import com.municipality.notificationservice.entity.Notification;
import com.municipality.notificationservice.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);
}