package com.example.symply_care.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private List<String> roles;
    private String refreshToken;

    public AuthenticationResponse(String accessToken, String refreshToken,
                                  Collection<? extends GrantedAuthority> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.roles = roles.stream().map(GrantedAuthority::getAuthority).toList();
    }
}