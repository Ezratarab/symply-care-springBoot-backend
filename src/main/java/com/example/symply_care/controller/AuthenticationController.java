package com.example.symply_care.controller;


import com.example.symply_care.dto.AuthenticationRequest;
import com.example.symply_care.dto.AuthenticationResponse;
import com.example.symply_care.dto.RefreshTokenRequest;
import com.example.symply_care.exceptions.AuthenticationServiceException;
import com.example.symply_care.exceptions.TokenRefreshException;
import com.example.symply_care.service.AuthenticationService;
import com.example.symply_care.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            AuthenticationResponse authResponse = authenticationService.authenticate(authenticationRequest);
            System.out.println("refresh token: " + authResponse.getRefreshToken());
            System.out.println("jwt token: " + authResponse.getAccessToken());
            System.out.println("roles: " + authResponse.getRoles());
            System.out.println("email: " + authenticationRequest.getEmail());
            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationServiceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            AuthenticationResponse authResponse = refreshTokenService.refresh(refreshTokenRequest.getRefreshToken());
            System.out.println("refresh token: " + authResponse.getRefreshToken());
            System.out.println("jwt token: " + authResponse.getAccessToken());
            return ResponseEntity.ok(authResponse);
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("test");
    }
}
