package com.example.symply_care.config;


import com.example.symply_care.service.CustomLogoutHandler;
import com.example.symply_care.service.CustomUserDetailsService;
import com.example.symply_care.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

@Configuration // מסמנים כך מחלקה שיוצרת הרבה beans על מנת שspring יוכל לעבד את כל הbeans ולקשר ביניהם
@EnableWebSecurity// מאפשר לspring למצוא ולהגדיר ראותו כמחלקה הראשית עבור webSecurity
@RequiredArgsConstructor //לייצר פעולה בונה עבור כל התכונות שהם final
public class AppSecurityChain {

    final private CustomUserDetailsService userDetailsService;
    final private JwtUtil jwtUtil;
    final private CustomLogoutHandler customLogoutHandler;


    @Bean //על מנת ליצור עצם של פעולה!! מבלי תוספת קוד
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/changePassword/**","/contactUs/**","/login/**", "/signup/**", "/doctors/doctors","/doctors/addDoctor","/patients/addPatient","/getSpecializations").permitAll()
                        .requestMatchers("/logout/**", "/refresh_token/**", "/doctors/**", "/rabbitmq/**",
                                "/patients/**", "/email/**").hasAnyRole("PATIENT", "DOCTOR")
                        .anyRequest().authenticated())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(customLogoutHandler)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                        .permitAll());

        return http.build();
    }


    //מנהל האימות בשביל spring
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }


    //מגדיר שנוכל לגשת לurl של static והלאה ללא צורך באימות
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("static/**");
    }
}