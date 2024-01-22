package com.example.symply_care.repository;

import com.example.symply_care.entity.RefreshToken;
import com.example.symply_care.entity.User;
import com.example.symply_care.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(Users byEmail);

    void deleteByUser(Users byEmail);

    Optional<RefreshToken> findByToken(String token);
}

