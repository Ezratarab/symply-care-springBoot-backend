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


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlackListService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authenticationRequest.getEmail());

        if (!authenticationRequest.getPassword().equals(userDetails.getPassword())) {
            throw new AuthenticationServiceException("Invalid credentials");
        }

        String jwtToken = jwtUtil.generateToken(authenticationRequest, userDetails);

        if (tokenBlacklistService.isBlacklisted(jwtToken)) {
            throw new AuthenticationServiceException("Invalid credentials");
        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

        return new AuthenticationResponse(jwtToken, refreshToken.getToken(), roles);
    }
}


