package com.municipality.shared.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {
    private Long id;
    
    @NotBlank(message = "Department name is required")
    private String name;
    
    private String description;
    private String contactEmail;
    private String contactPhone;
    private List<UserDto> staff;
}