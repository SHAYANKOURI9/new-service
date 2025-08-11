package com.municipality.departmentservice.service;

import com.municipality.departmentservice.client.UserServiceClient;
import com.municipality.departmentservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserServiceClient userServiceClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // This is a simplified implementation for the department service
            // In a real scenario, you might want to cache user details or implement differently
            UserDto userDto = userServiceClient.getUserById(Long.parseLong(username));
            
            return new User(
                    userDto.getUsername(),
                    "N/A", // Password not needed for JWT validation
                    userDto.isEnabled(),
                    true,
                    true,
                    true,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userDto.getRole()))
            );
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}