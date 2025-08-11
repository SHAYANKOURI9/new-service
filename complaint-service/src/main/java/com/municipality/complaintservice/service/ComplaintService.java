package com.municipality.complaintservice.service;

import com.municipality.complaintservice.client.DepartmentServiceClient;
import com.municipality.complaintservice.client.NotificationServiceClient;
import com.municipality.complaintservice.client.UserServiceClient;
import com.municipality.complaintservice.dto.*;
import com.municipality.complaintservice.entity.Complaint;
import com.municipality.complaintservice.entity.ComplaintCategory;
import com.municipality.complaintservice.entity.ComplaintStatus;
import com.municipality.complaintservice.entity.Comment;
import com.municipality.complaintservice.repository.ComplaintRepository;
import com.municipality.complaintservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintService {
    
    private final ComplaintRepository complaintRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final DepartmentServiceClient departmentServiceClient;
    private final NotificationServiceClient notificationServiceClient;
    
    public ComplaintDto createComplaint(ComplaintDto complaintDto) {
        Complaint complaint = new Complaint();
        complaint.setCategory(ComplaintCategory.valueOf(complaintDto.getCategory()));
        complaint.setDescription(complaintDto.getDescription());
        complaint.setLocation(complaintDto.getLocation());
        complaint.setUserId(complaintDto.getUserId());
        complaint.setStatus(ComplaintStatus.PENDING);
        
        Complaint savedComplaint = complaintRepository.save(complaint);
        return convertToDto(savedComplaint);
    }
    
    public Optional<ComplaintDto> getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public List<ComplaintDto> getAllComplaints() {
        return complaintRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ComplaintDto> getComplaintsByUserId(Long userId) {
        return complaintRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ComplaintDto> getComplaintsByStatus(String status) {
        ComplaintStatus complaintStatus = ComplaintStatus.valueOf(status.toUpperCase());
        return complaintRepository.findByStatus(complaintStatus).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ComplaintDto> getComplaintsByCategory(String category) {
        ComplaintCategory complaintCategory = ComplaintCategory.valueOf(category.toUpperCase());
        return complaintRepository.findByCategory(complaintCategory).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ComplaintDto updateComplaintStatus(Long id, String status) {
        return complaintRepository.findById(id)
                .map(complaint -> {
                    complaint.setStatus(ComplaintStatus.valueOf(status.toUpperCase()));
                    Complaint updatedComplaint = complaintRepository.save(complaint);
                    
                    // Send notification
                    sendStatusUpdateNotification(updatedComplaint, status);
                    
                    return convertToDto(updatedComplaint);
                })
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }
    
    public ComplaintDto assignComplaint(Long id, Long departmentId, Long staffId) {
        return complaintRepository.findById(id)
                .map(complaint -> {
                    complaint.setAssignedDepartmentId(departmentId);
                    complaint.setAssignedStaffId(staffId);
                    complaint.setStatus(ComplaintStatus.ASSIGNED);
                    Complaint updatedComplaint = complaintRepository.save(complaint);
                    
                    // Send notification
                    sendAssignmentNotification(updatedComplaint);
                    
                    return convertToDto(updatedComplaint);
                })
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }
    
    public CommentDto addComment(Long complaintId, CommentDto commentDto) {
        return complaintRepository.findById(complaintId)
                .map(complaint -> {
                    Comment comment = new Comment();
                    comment.setContent(commentDto.getContent());
                    comment.setUserId(commentDto.getUserId());
                    comment.setComplaint(complaint);
                    
                    Comment savedComment = commentRepository.save(comment);
                    return convertToCommentDto(savedComment);
                })
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }
    
    public List<CommentDto> getCommentsByComplaintId(Long complaintId) {
        return commentRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId).stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());
    }
    
    private ComplaintDto convertToDto(Complaint complaint) {
        ComplaintDto dto = new ComplaintDto();
        dto.setId(complaint.getId());
        dto.setCategory(complaint.getCategory().name());
        dto.setDescription(complaint.getDescription());
        dto.setLocation(complaint.getLocation());
        dto.setStatus(complaint.getStatus().name());
        dto.setUserId(complaint.getUserId());
        dto.setAssignedDepartmentId(complaint.getAssignedDepartmentId());
        dto.setAssignedStaffId(complaint.getAssignedStaffId());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());
        
        // Get user details
        try {
            UserDto user = userServiceClient.getUserById(complaint.getUserId());
            dto.setUserName(user.getName());
        } catch (Exception e) {
            dto.setUserName("Unknown User");
        }
        
        // Get department details
        if (complaint.getAssignedDepartmentId() != null) {
            try {
                DepartmentDto department = departmentServiceClient.getDepartmentById(complaint.getAssignedDepartmentId());
                dto.setAssignedDepartmentName(department.getName());
            } catch (Exception e) {
                dto.setAssignedDepartmentName("Unknown Department");
            }
        }
        
        // Get staff details
        if (complaint.getAssignedStaffId() != null) {
            try {
                UserDto staff = userServiceClient.getUserById(complaint.getAssignedStaffId());
                dto.setAssignedStaffName(staff.getName());
            } catch (Exception e) {
                dto.setAssignedStaffName("Unknown Staff");
            }
        }
        
        // Get comments
        dto.setComments(getCommentsByComplaintId(complaint.getId()));
        
        return dto;
    }
    
    private CommentDto convertToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUserId(comment.getUserId());
        dto.setCreatedAt(comment.getCreatedAt());
        
        // Get user details
        try {
            UserDto user = userServiceClient.getUserById(comment.getUserId());
            dto.setUserName(user.getName());
            dto.setUserRole(user.getRole());
        } catch (Exception e) {
            dto.setUserName("Unknown User");
            dto.setUserRole("UNKNOWN");
        }
        
        return dto;
    }
    
    private void sendStatusUpdateNotification(Complaint complaint, String status) {
        try {
            UserDto user = userServiceClient.getUserById(complaint.getUserId());
            NotificationRequest request = new NotificationRequest();
            request.setUserId(user.getId());
            request.setEmail(user.getEmail());
            request.setSubject("Complaint Status Update");
            request.setMessage("Your complaint #" + complaint.getId() + " status has been updated to: " + status);
            request.setNotificationType("STATUS_UPDATE");
            
            notificationServiceClient.sendNotification(request);
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
    
    private void sendAssignmentNotification(Complaint complaint) {
        try {
            UserDto user = userServiceClient.getUserById(complaint.getUserId());
            NotificationRequest request = new NotificationRequest();
            request.setUserId(user.getId());
            request.setEmail(user.getEmail());
            request.setSubject("Complaint Assigned");
            request.setMessage("Your complaint #" + complaint.getId() + " has been assigned to a department for resolution.");
            request.setNotificationType("ASSIGNMENT");
            
            notificationServiceClient.sendNotification(request);
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
}