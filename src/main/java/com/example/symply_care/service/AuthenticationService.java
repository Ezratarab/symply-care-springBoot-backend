package com.example.symply_care.service;

import com.example.symply_care.dto.AuthenticationRequest;
import com.example.symply_care.dto.AuthenticationResponse;
import com.example.symply_care.entity.RefreshToken;
import com.example.symply_care.exceptions.AuthenticationServiceException;
import com.example.symply_care.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * This class contains the business logic for authenticating a user
 * it takes in an AuthenticationRequest object, which contains the username and password
 * it returns an AuthenticationResponse object, which contains the JWT and refresh token, and the user's roles, but before returning the response,
 * it checks if the password matches the password in the database, and if the token is already blacklisted
 */

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlackListService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;

    // The authenticate() method takes in an AuthenticationRequest object, which contains the username and password.
    // The method returns an AuthenticationResponse object, which contains the JWT and refresh token, and the user's roles.
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        // load the user details from the database using the username by calling the loadUserByUsername() method
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authenticationRequest.getEmail());

        // check if the password matches the password in the database
        if (!authenticationRequest.getPassword().equals(userDetails.getPassword())) {
            throw new AuthenticationServiceException("Invalid credentials");
        }

        // generate the JWT token
        String jwtToken = jwtUtil.generateToken(authenticationRequest, userDetails);

        // check if the token is already blacklisted
        if (tokenBlacklistService.isBlacklisted(jwtToken)) {
            throw new AuthenticationServiceException("Invalid credentials");
        }

        // create a refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        // get the user's roles
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

        // return the AuthenticationResponse object
        return new AuthenticationResponse(jwtToken, refreshToken.getToken(), roles);
    }
}


