package com.municipality.notificationservice.service;

import com.municipality.notificationservice.dto.NotificationRequest;
import com.municipality.notificationservice.entity.Notification;
import com.municipality.notificationservice.entity.NotificationStatus;
import com.municipality.notificationservice.entity.NotificationType;
import com.municipality.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final JavaMailSender emailSender;
    
    public void sendNotification(NotificationRequest request) {
        try {
            // Create notification record
            Notification notification = new Notification();
            notification.setUserId(request.getUserId());
            notification.setEmail(request.getEmail());
            notification.setSubject(request.getSubject());
            notification.setMessage(request.getMessage());
            notification.setNotificationType(NotificationType.valueOf(request.getNotificationType()));
            notification.setStatus(NotificationStatus.PENDING);
            
            Notification savedNotification = notificationRepository.save(notification);
            
            // Send email
            sendEmailNotification(savedNotification);
            
            // Update status to sent
            savedNotification.setStatus(NotificationStatus.SENT);
            savedNotification.setSentAt(LocalDateTime.now());
            notificationRepository.save(savedNotification);
            
            log.info("Notification sent successfully to user: {}", request.getUserId());
            
        } catch (Exception e) {
            log.error("Failed to send notification to user: {}", request.getUserId(), e);
            
            // Update status to failed
            Notification failedNotification = new Notification();
            failedNotification.setUserId(request.getUserId());
            failedNotification.setEmail(request.getEmail());
            failedNotification.setSubject(request.getSubject());
            failedNotification.setMessage(request.getMessage());
            failedNotification.setNotificationType(NotificationType.valueOf(request.getNotificationType()));
            failedNotification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(failedNotification);
        }
    }
    
    private void sendEmailNotification(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getEmail());
        message.setSubject(notification.getSubject());
        message.setText(notification.getMessage());
        
        emailSender.send(message);
    }
    
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }
    
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    public List<Notification> getNotificationsByStatus(String status) {
        NotificationStatus notificationStatus = NotificationStatus.valueOf(status.toUpperCase());
        return notificationRepository.findByStatus(notificationStatus);
    }
    
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}