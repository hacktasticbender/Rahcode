package com.ticketing.backend.controller;

import com.ticketing.backend.dto.ApiResponse;
import com.ticketing.backend.dto.UserDto;
import com.ticketing.backend.entity.Role;
import com.ticketing.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDto user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @GetMapping("/support-agents")
    public ResponseEntity<ApiResponse<List<UserDto>>> getSupportAgents() {
        List<UserDto> supportAgents = userService.getUsersByRole(Role.SUPPORT_AGENT);
        return ResponseEntity.ok(ApiResponse.success(supportAgents));
    }
}