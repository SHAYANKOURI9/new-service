package com.municipality.userservice.controller;

import com.municipality.userservice.dto.AuthRequest;
import com.municipality.userservice.dto.AuthResponse;
import com.municipality.userservice.dto.UserDto;
import com.municipality.userservice.entity.User;
import com.municipality.userservice.entity.UserRole;
import com.municipality.userservice.security.JwtUtil;
import com.municipality.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name(), user.getId());

        AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRole().name(), user.getName(), user.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setRole(UserRole.valueOf(userDto.getRole()));

        User savedUser = userService.createUser(user);
        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole().name(), savedUser.getId());

        AuthResponse response = new AuthResponse(token, savedUser.getUsername(), savedUser.getRole().name(), savedUser.getName(), savedUser.getId());
        return ResponseEntity.ok(response);
    }
}