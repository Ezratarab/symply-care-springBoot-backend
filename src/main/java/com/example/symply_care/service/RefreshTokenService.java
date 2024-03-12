package com.example.symply_care.service;


import com.example.symply_care.dto.AuthenticationResponse;
import com.example.symply_care.entity.RefreshToken;
import com.example.symply_care.entity.User;
import com.example.symply_care.entity.Users;
import com.example.symply_care.exceptions.TokenRefreshException;
import com.example.symply_care.repository.RefreshTokenRepository;
import com.example.symply_care.repository.UsersRepository;
import com.example.symply_care.util.JwtProperties;
import com.example.symply_care.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    final private RefreshTokenRepository refreshTokenRepository;
    final private UsersRepository usersRepository;
    final private CustomUserDetailsService customUserDetailsService;
    final private JwtUtil jwtUtil;

    @Transactional
    public RefreshToken createRefreshToken(String email) {

        Optional<Users> user = usersRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }

        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByUser(user.get());
        if (refreshTokenOptional.isPresent()) {
            deleteRefreshTokenForUser(email);
        }

        try {
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .user(user.get())
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(JwtProperties.EXPIRATION_TIME_FOR_REFRESH_TOKEN))
                    .build();

            return refreshTokenRepository.save(newRefreshToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating refresh token");
        }
    }

   @Transactional
    public void deleteRefreshTokenForUser(String email) {
        Optional<Users> user = usersRepository.findByEmail(email);
        refreshTokenRepository.deleteByUser(user.get());
        refreshTokenRepository.flush();
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public boolean verifyExpiration(RefreshToken refreshToken) {

        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            refreshTokenRepository.flush();
            System.out.println("Refresh Token has expired. Please login again");
            return false;
        }

        return true;
    }

    @Transactional
    public AuthenticationResponse refresh(String refreshToken) {
        Optional<RefreshToken> refreshTokenEntity = findByToken(refreshToken);
        refreshTokenEntity.orElseThrow(() -> new TokenRefreshException("Refresh token is invalid"));

        RefreshToken validRefreshToken = refreshTokenEntity.get();
        if (!verifyExpiration(validRefreshToken)) {
            throw new TokenRefreshException("Refresh token has expired. Please log in again");
        }

        String userEmail = validRefreshToken.getUser().getEmail();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
        String newJwtToken = jwtUtil.generateTokenFromUsername(userDetails.getUsername());
        RefreshToken newRefreshToken = createRefreshToken(userEmail);
        return new AuthenticationResponse(newJwtToken, newRefreshToken.getToken(), userDetails.getAuthorities());
    }
}

