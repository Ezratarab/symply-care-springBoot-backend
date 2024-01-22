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

    // test
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("test");
    }


    // The authenticateUser() method takes in an AuthenticationRequest object, which contains the username and password.
    // The method returns an AuthenticationResponse object, which contains the JWT and refresh token, and the user's roles.
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            // authenticate() method throws an AuthenticationServiceException if the username or password is invalid.
            AuthenticationResponse authResponse = authenticationService.authenticate(authenticationRequest);

            // print out the tokens, roles, and username, for testing
            System.out.println("refresh token: " + authResponse.getRefreshToken());
            System.out.println("jwt token: " + authResponse.getAccessToken());
            System.out.println("roles: " + authResponse.getRoles());
            System.out.println("email: " + authenticationRequest.getEmail());

            // if the username and password are valid, the method returns an AuthenticationResponse object OK response.
            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationServiceException e) {
            // if the username or password is invalid, the method returns a 401 Unauthorized response.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // The refreshToken() method takes in a RefreshTokenRequest object, which contains the refresh token.
    // The method returns an AuthenticationResponse object, which contains the JWT and refresh token, and the user's roles.
    @PostMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            // try to refresh the token, if the refresh token is valid, the method returns an AuthenticationResponse object OK response.
            AuthenticationResponse authResponse = refreshTokenService.refresh(refreshTokenRequest.getRefreshToken());
            System.out.println("refresh token: " + authResponse.getRefreshToken());
            System.out.println("jwt token: " + authResponse.getAccessToken());
            return ResponseEntity.ok(authResponse);
        } catch (TokenRefreshException e) {
            // else, the method returns a 401 Unauthorized response.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
