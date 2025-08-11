package com.municipality.departmentservice.service;

import com.municipality.departmentservice.client.UserServiceClient;
import com.municipality.departmentservice.dto.DepartmentDto;
import com.municipality.departmentservice.dto.UserDto;
import com.municipality.departmentservice.entity.Department;
import com.municipality.departmentservice.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final UserServiceClient userServiceClient;
    
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        Department department = new Department();
        department.setName(departmentDto.getName());
        department.setDescription(departmentDto.getDescription());
        department.setContactEmail(departmentDto.getContactEmail());
        department.setContactPhone(departmentDto.getContactPhone());
        
        Department savedDepartment = departmentRepository.save(department);
        return convertToDto(savedDepartment);
    }
    
    public Optional<DepartmentDto> getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public DepartmentDto updateDepartment(Long id, DepartmentDto departmentDto) {
        return departmentRepository.findById(id)
                .map(department -> {
                    department.setName(departmentDto.getName());
                    department.setDescription(departmentDto.getDescription());
                    department.setContactEmail(departmentDto.getContactEmail());
                    department.setContactPhone(departmentDto.getContactPhone());
                    
                    Department updatedDepartment = departmentRepository.save(department);
                    return convertToDto(updatedDepartment);
                })
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }
    
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
    
    public DepartmentDto assignStaffToDepartment(Long departmentId, Long staffId) {
        return departmentRepository.findById(departmentId)
                .map(department -> {
                    if (!department.getStaffIds().contains(staffId)) {
                        department.getStaffIds().add(staffId);
                        Department updatedDepartment = departmentRepository.save(department);
                        return convertToDto(updatedDepartment);
                    }
                    return convertToDto(department);
                })
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }
    
    public DepartmentDto removeStaffFromDepartment(Long departmentId, Long staffId) {
        return departmentRepository.findById(departmentId)
                .map(department -> {
                    department.getStaffIds().remove(staffId);
                    Department updatedDepartment = departmentRepository.save(department);
                    return convertToDto(updatedDepartment);
                })
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }
    
    public List<UserDto> getAvailableStaff() {
        try {
            return userServiceClient.getStaffUsers();
        } catch (Exception e) {
            return List.of();
        }
    }
    
    private DepartmentDto convertToDto(Department department) {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        dto.setContactEmail(department.getContactEmail());
        dto.setContactPhone(department.getContactPhone());
        dto.setCreatedAt(department.getCreatedAt());
        dto.setUpdatedAt(department.getUpdatedAt());
        
        // Get staff details
        List<UserDto> staff = department.getStaffIds().stream()
                .map(staffId -> {
                    try {
                        return userServiceClient.getUserById(staffId);
                    } catch (Exception e) {
                        UserDto unknownUser = new UserDto();
                        unknownUser.setId(staffId);
                        unknownUser.setName("Unknown User");
                        unknownUser.setEmail("unknown@example.com");
                        unknownUser.setUsername("unknown");
                        unknownUser.setRole("STAFF");
                        unknownUser.setEnabled(false);
                        return unknownUser;
                    }
                })
                .collect(Collectors.toList());
        
        dto.setStaff(staff);
        return dto;
    }
}