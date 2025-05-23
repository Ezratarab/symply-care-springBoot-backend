package com.example.symply_care.config;


import com.example.symply_care.service.CustomUserDetailsService;
import com.example.symply_care.util.JwtProperties;
import com.example.symply_care.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { //OncePerRequestFilter מאפשר הרצה עבור כל בקשה

    final private JwtUtil jwtUtil;
    final private CustomUserDetailsService customUserDetailsService;


    //עושה פילטר ראשוני עבור כל בקשה לאימות הנתונים
    //מסומן בprotected על מנת שרק המחלקות היורשות שקשורות לspring security יוכלו לקרוא לה
    //פונקציה זו דורסת את הפונקציה של האב
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(JwtProperties.HEADER_STRING);
        String token;

        if (header != null && header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            token = header.substring(JwtProperties.TOKEN_PREFIX.length());
        } else {
            token = request.getParameter("token");
        }

        if (token != null) {
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response); // המשך הפילטור או המשך מימוש הבקשה לפי הקוד זאת אומרת שימשיך במסלולו הרגיל
        //במידה ולא יהיה שורה זאת אז הבקשה תיעצר פה
    }
}

