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
/** OncePerRequestFilter is a base class for filters which need to do some sort of processing a single time per request
 as opposed to once per dispatch. For example, a filter which examines the HTTP request headers, or consults the HttpSession,
 or does something else that is independent of the servlet path.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    final private JwtUtil jwtUtil;
    final private CustomUserDetailsService customUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // retrieve the Authorization header from the request
        String header = request.getHeader(JwtProperties.HEADER_STRING);
        String token;

        // extract the token from the header if it's present
        if (header != null && header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            token = header.substring(JwtProperties.TOKEN_PREFIX.length());
        } else {
            // alternatively, try to get the token from a query parameter
            token = request.getParameter("token");
        }

        if (token != null) {
            // extract the username from the token
            String username = jwtUtil.extractUsername(token);
            // load user details using the extracted username
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // validate the token with the loaded user details
            if (jwtUtil.validateToken(token, userDetails)) {
                // create an authentication object and set it in the Security Context
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // pass the request along the filter chain
        filterChain.doFilter(request, response);
    }
}

