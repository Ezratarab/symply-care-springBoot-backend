package com.example.symply_care.service;


import com.example.symply_care.util.JwtProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom logout handler, which adds the token to the blacklist, after the user logs out
 *
 */


@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutSuccessHandler {

    final private TokenBlackListService tokenBlacklistService;


    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws
            IOException, ServletException {

        // Extract the JWT token
        String token = request.getHeader(JwtProperties.HEADER_STRING).
                substring(JwtProperties.HEADER_STRING.length());
        System.out.println("--------------------------------------");
        tokenBlacklistService.addToBlacklist(token);

        // write user logout
        if (authentication != null)
            System.out.println("User logged out: " + authentication.getName());


    }
}
