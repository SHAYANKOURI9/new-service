package com.municipality.departmentservice.client;

import com.municipality.departmentservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable Long id);
    
    @GetMapping("/api/users/role/STAFF")
    List<UserDto> getStaffUsers();
}