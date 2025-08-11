package com.municipality.complaintservice.controller;

import com.municipality.complaintservice.dto.CommentDto;
import com.municipality.complaintservice.dto.ComplaintDto;
import com.municipality.complaintservice.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ComplaintDto> createComplaint(@Valid @RequestBody ComplaintDto complaintDto) {
        ComplaintDto createdComplaint = complaintService.createComplaint(complaintDto);
        return ResponseEntity.ok(createdComplaint);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintDto> getComplaintById(@PathVariable Long id) {
        return complaintService.getComplaintById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<ComplaintDto>> getAllComplaints() {
        List<ComplaintDto> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<ComplaintDto>> getComplaintsByUserId(@PathVariable Long userId) {
        List<ComplaintDto> complaints = complaintService.getComplaintsByUserId(userId);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<ComplaintDto>> getComplaintsByStatus(@PathVariable String status) {
        List<ComplaintDto> complaints = complaintService.getComplaintsByStatus(status);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<ComplaintDto>> getComplaintsByCategory(@PathVariable String category) {
        List<ComplaintDto> complaints = complaintService.getComplaintsByCategory(category);
        return ResponseEntity.ok(complaints);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ComplaintDto> updateComplaintStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ComplaintDto updatedComplaint = complaintService.updateComplaintStatus(id, status);
        return ResponseEntity.ok(updatedComplaint);
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ComplaintDto> assignComplaint(
            @PathVariable Long id,
            @RequestParam Long departmentId,
            @RequestParam Long staffId) {
        ComplaintDto assignedComplaint = complaintService.assignComplaint(id, departmentId, staffId);
        return ResponseEntity.ok(assignedComplaint);
    }

    @PostMapping("/{complaintId}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long complaintId,
            @Valid @RequestBody CommentDto commentDto) {
        CommentDto addedComment = complaintService.addComment(complaintId, commentDto);
        return ResponseEntity.ok(addedComment);
    }

    @GetMapping("/{complaintId}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByComplaintId(@PathVariable Long complaintId) {
        List<CommentDto> comments = complaintService.getCommentsByComplaintId(complaintId);
        return ResponseEntity.ok(comments);
    }
}