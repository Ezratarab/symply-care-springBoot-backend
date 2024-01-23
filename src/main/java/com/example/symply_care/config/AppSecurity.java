package com.example.symply_care.config;


import com.example.symply_care.service.CustomLogoutHandler;
import com.example.symply_care.service.CustomUserDetailsService;
import com.example.symply_care.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AppSecurity {

    // Inject the UserDetailsService implementation, using  RequiredArgsConstructor annotation of Lombok
    final private CustomUserDetailsService userDetailsService;
    final private JwtUtil jwtUtil;
    final private CustomLogoutHandler customLogoutHandler;

    // Bean annotation is used to declare a PasswordEncoder bean, it is used to encode passwords
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // we don't need csrf protection in jwt
        http
                // disable csrf and cors,
                // for authentication and authorization, not csrf or cors, csrf is for form-based authentication csrf means cross-site request forgery
                // cors means cross-origin resource sharing - it is for cross-origin requests - we don't need them in jwt
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())

                // The SessionCreationPolicy.STATELESS setting means that the application will not create or use HTTP sessions.
                // This is a common configuration in RESTFUL APIs, especially when using token-based authentication like JWT (JSON Web Token).
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // adding a custom JWT authentication filter
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class)

                // Configuring authorization for HTTP requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/test/**", "/login/**", "/logout/**","/home/**",
                                "/refresh_token/**","/doctors/**" ,"/patients/**","/home/**","/signup/**","/about/**","/cntactus/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("DOCTOR")


                        .anyRequest().authenticated())

                .logout(logout -> logout
                        // logoutUrl() - the URL that triggers log out to occur (default is "/logout").
                        .logoutUrl("/logout")
                        // logoutSuccessUrl() - the URL to redirect to after logout has occurred.
                        .logoutSuccessHandler(customLogoutHandler)
                        // invalidateHttpSession() - whether to invalidate the HttpSession at the time of logout (default is true).
                        .invalidateHttpSession(true)
                        // deleteCookies() - the names of cookies to be removed on logout success (if any).
                        .deleteCookies("JSESSIONID")
                        // clearAuthentication() - whether to clear the Authentication at the time of logout (default is true).
                        .clearAuthentication(true)
                        // permitAll() - allow any user to call the logout URL
                        .permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        // The AuthenticationConfiguration class is used to create an AuthenticationManager instance.
        // it is used to create an AuthenticationManager instance, in this case, we are using the default one
        return configuration.getAuthenticationManager();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // ignore static resources, so that we can access them without authentication
        return (web) -> web.ignoring().requestMatchers("static/**");
    }
}