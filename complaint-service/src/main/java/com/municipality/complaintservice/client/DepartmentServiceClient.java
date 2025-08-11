package com.municipality.complaintservice.client;

import com.municipality.complaintservice.dto.DepartmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "department-service")
public interface DepartmentServiceClient {
    
    @GetMapping("/api/departments/{id}")
    DepartmentDto getDepartmentById(@PathVariable Long id);
}