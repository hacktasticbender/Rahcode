package com.ticketing.backend.controller;

import com.ticketing.backend.dto.*;
import com.ticketing.backend.security.JwtUtils;
import com.ticketing.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDto userDto = userService.getUserByUsername(userDetails.getUsername());
        
        JwtResponse jwtResponse = new JwtResponse(jwt, 
                                                userDto.getId(),
                                                userDto.getUsername(),
                                                userDto.getEmail(),
                                                userDto.getRole().toString());
        
        return ResponseEntity.ok(ApiResponse.success("Login successful", jwtResponse));
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDto userDto = userService.createUser(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", userDto));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("User logged out successfully", null));
    }
}