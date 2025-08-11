package com.municipality.departmentservice.controller;

import com.municipality.departmentservice.dto.DepartmentDto;
import com.municipality.departmentservice.dto.UserDto;
import com.municipality.departmentservice.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentDto> createDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
        DepartmentDto createdDepartment = departmentService.createDepartment(departmentDto);
        return ResponseEntity.ok(createdDepartment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) {
        return departmentService.getDepartmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentDto> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentDto departmentDto) {
        DepartmentDto updatedDepartment = departmentService.updateDepartment(id, departmentDto);
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{departmentId}/staff/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentDto> assignStaffToDepartment(
            @PathVariable Long departmentId,
            @PathVariable Long staffId) {
        DepartmentDto updatedDepartment = departmentService.assignStaffToDepartment(departmentId, staffId);
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/{departmentId}/staff/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentDto> removeStaffFromDepartment(
            @PathVariable Long departmentId,
            @PathVariable Long staffId) {
        DepartmentDto updatedDepartment = departmentService.removeStaffFromDepartment(departmentId, staffId);
        return ResponseEntity.ok(updatedDepartment);
    }

    @GetMapping("/staff/available")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAvailableStaff() {
        List<UserDto> availableStaff = departmentService.getAvailableStaff();
        return ResponseEntity.ok(availableStaff);
    }
}