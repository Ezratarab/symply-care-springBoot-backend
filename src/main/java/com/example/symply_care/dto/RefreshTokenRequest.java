package com.example.symply_care.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
    private String jwtToken;
}
