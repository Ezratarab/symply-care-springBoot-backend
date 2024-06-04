package com.example.symply_care.controller;


import com.example.symply_care.dto.*;
import com.example.symply_care.entity.*;
import com.example.symply_care.exceptions.AuthenticationServiceException;
import com.example.symply_care.exceptions.TokenRefreshException;
import com.example.symply_care.repository.DoctorRepository;
import com.example.symply_care.repository.PatientRepository;
import com.example.symply_care.repository.UsersRepository;
import com.example.symply_care.service.AuthenticationService;
import com.example.symply_care.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final RabbitMQController rabbitMQController;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UsersRepository usersRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            AuthenticationResponse authResponse = authenticationService.authenticate(authenticationRequest);
            System.out.println("refresh token: " + authResponse.getRefreshToken());
            System.out.println("jwt token: " + authResponse.getAccessToken());
            System.out.println("roles: " + authResponse.getRoles());
            System.out.println("email: " + authenticationRequest.getEmail());
            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationServiceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            AuthenticationResponse authResponse = refreshTokenService.refresh(refreshTokenRequest.getRefreshToken());
            System.out.println("refresh token: " + authResponse.getRefreshToken());
            System.out.println("jwt token: " + authResponse.getAccessToken());
            return ResponseEntity.ok(authResponse);
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("test");
    }

    @PostMapping("/contactUs/{email}")
    public ResponseEntity<?> contactUs(@RequestBody String message, @PathVariable String email){
        RabbitMQMessage rabbitMQContactUsMessage = new RabbitMQMessage();
        rabbitMQContactUsMessage.setMessage(message);
        rabbitMQContactUsMessage.setEmail(email);
        return rabbitMQController.sendMessageToAdmin(rabbitMQContactUsMessage);
    }
    @GetMapping("/getSpecializations")
    public List<Specialization> getAllSpecializations() {
        return Arrays.asList(Specialization.values());
    }
    @PostMapping("/changePassword/{email}")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, Object> data, @PathVariable String email) throws Exception {
        try {
            String newPassword = (String) data.get("password");
            Long id = Long.parseLong((String) data.get("id")); // Convert id to Long

            Optional<Patient> patientResponse = patientRepository.findByEmail(email);
            Optional<Doctor> doctorResponse = doctorRepository.findByEmail(email);
            Optional<Users> usersOptional = usersRepository.findByEmail(email);

            if (patientResponse.isEmpty()&&doctorResponse.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else if (patientResponse.isPresent()) {
                if(!Objects.equals(patientResponse.get().getId(), id)){
                    return ResponseEntity.badRequest().build();
                }
                else {
                    patientResponse.get().setPassword(newPassword);
                    patientRepository.save(patientResponse.get());
                    usersOptional.get().setPassword(newPassword);
                    usersRepository.save(usersOptional.get());
                    return ResponseEntity.ok().build();
                }
            }else{
                if(!Objects.equals(doctorResponse.get().getId(), id)){
                    return ResponseEntity.badRequest().build();
                }
                else {
                    doctorResponse.get().setPassword(newPassword);
                    doctorRepository.save(doctorResponse.get());
                    usersOptional.get().setPassword(newPassword);
                    usersRepository.save(usersOptional.get());
                    return ResponseEntity.ok().build();
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

}
