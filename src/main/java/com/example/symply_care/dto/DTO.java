package com.example.symply_care.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class DTO {

    @NotNull(message = "user id should not be null")
    private Long id;

    @NotNull(message = "first name should not be null")
    @NotBlank(message = "first name should not be blank")
    @NotEmpty(message = "first name should not be empty")
    private String firstName;

    @NotNull(message = "last name should not be null")
    @NotBlank(message = "last name should not be blank")
    @NotEmpty(message = "last name should not be empty")
    private String lastName;

    @NotNull(message = "email should not be null")
    @NotBlank(message = "email should not be blank")
    @NotEmpty(message = "email should not be empty")
    @Email(message = "email should be valid")
    @Column(unique = true, nullable = false, length = 70)
    private String email;


    @NotNull(message = "password should not be null")
    @NotBlank(message = "password should not be blank")
    @NotEmpty(message = "password should not be empty")
    private String password;


    private String city;

    private String country;

    private String street;

    @NotNull(message = "Birth Date should not be null")
    @NotBlank(message = "Birth Date should not be blank")
    @NotEmpty(message = "Birth Date should not be empty")
    private Date birthDay;

    @Lob
    private byte[] imageData;
}
