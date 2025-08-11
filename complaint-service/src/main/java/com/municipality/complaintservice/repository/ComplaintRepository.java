package com.municipality.complaintservice.repository;

import com.municipality.complaintservice.entity.Complaint;
import com.municipality.complaintservice.entity.ComplaintCategory;
import com.municipality.complaintservice.entity.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUserId(Long userId);
    List<Complaint> findByStatus(ComplaintStatus status);
    List<Complaint> findByCategory(ComplaintCategory category);
    List<Complaint> findByAssignedDepartmentId(Long departmentId);
    List<Complaint> findByAssignedStaffId(Long staffId);
    List<Complaint> findByUserIdAndStatus(Long userId, ComplaintStatus status);
}