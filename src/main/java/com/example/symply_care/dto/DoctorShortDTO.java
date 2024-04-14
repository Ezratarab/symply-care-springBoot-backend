package com.example.symply_care.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DoctorShortDTO {

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

    @Lob
    private byte[] imageData;

    @NotNull(message = "specialization should not be null")
    @NotBlank(message = "specialization should not be blank")
    @NotEmpty(message = "specialization should not be empty")
    private String specialization;

    @NotNull(message = "hospital should not be null")
    @NotBlank(message = "hospital should not be blank")
    @NotEmpty(message = "hospital should not be empty")
    private String hospital;

    @NotNull(message = "HMO should not be null")
    @NotBlank(message = "HMO should not be blank")
    @NotEmpty(message = "HMO should not be empty")
    private String hmo;

    @NotNull(message = "experience should not be null")
    @NotBlank(message = "experience should not be blank")
    @NotEmpty(message = "experience should not be empty")
    private Integer experience;
}
